# DMA - Labo 3
Auteurs : Bastian Chollet / Kevin Ferati

# 1 - Lister les bornes 
## Implémentation

Dans `MainActivity.kt` : 
- Scan initial des bornes compatibles avec WifiRTT 
```kt
 val apsInRange = wifiManager.scanResults
 .filter { it.is80211mcResponder }
 .take(RangingRequest.getMaxPeers())
```

- Initialisation du callback et début de l'écoute. Ici, on passe au VM uniquement les résultats du callback qui ne sont pas erronés
```kt
  val req = RangingRequest.Builder().addAccessPoints(apsInRange).build()
wifiRttManager.startRanging(req, mainExecutor, object : RangingResultCallback() {
    override fun onRangingResults(results: List<RangingResult>) {
        wifiRttViewModel.onNewRangingResults(results.filter { it.status == STATUS_SUCCESS })
    }
    override fun onRangingFailure(code: Int) { }
})
```

Dans `WifiRTTViewModel.kt`, fonction `onNewRangingResults` : 

- D'abord, on itère sur les résultats reçus, ajoutons les nouveaux et mettons à jours les anciens :
```kt
 // existing ones
newResults.forEach { rangingResult ->

    val existingAp = _rangedAccessPoints.value!!
        .find { it.bssid == rangingResult.macAddress.toString() }

    if (existingAp == null) {
        newState.add(RangedAccessPoint.newInstance(rangingResult))
    } else {
        existingAp.update(rangingResult)
        newState.add(existingAp)

    }
}
```

- Ensuite, on repasse sur les AP déjà existants pour retirer ceux qui sont trop vieux : 

```kt
_rangedAccessPoints.value!!
    .filter { ap -> newResults.find { it.macAddress.toString() == ap.bssid } == null} // keep only those who haven't been updated
    .forEach {
        if (System.currentTimeMillis() - it.age <= maxAgeMs) {
            // Keep only the youngest ones
            newState.add(it)
        }
}
```


## Question 1.1 


Les fenêtres ne semblent pas influencer de manière significative la qualité de la localisation. Cependant, les objets comme les tables, murs ainsi que les moins-objets comme des êtres humains l'impactent fortement. En mettant simplement le mobile sous la table, on a une perte de précision allant  jusqu'à 2m environ. La moyenne permet effectivement d'avoir une mesure plus fiable, car les estimations de distance suivent a priori une estimation gaussienne, du moment qu'il n'y a pas de valeurs aberrantes.

# 2 - Déterminer la position du smartphone
## Implémentation

- Dans la méthode `estimateLocation` nous récupérons les positions et les distances de la façon suivante

```kt
val rangedAccessPoints = _rangedAccessPoints.value!!

val distances = rangedAccessPoints
	.filter { it.bssid in mapConfig.value!!.accessPointKnownLocations.keys}
	.map { it.distanceMm }
	.toDoubleArray()

val positions =
	rangedAccessPoints.mapNotNull { mapConfig.value!!.accessPointKnownLocations[it.bssid] }
	.map { doubleArrayOf(it.xMm.toDouble(), it.yMm.toDouble()) }
	.toTypedArray()

if(distances.size < 3)
	return
```

Nous filtrons les différents APs pour ne garder que ceux dont nous connaissons la position physique dans la `mapConfig`.

## Question 2.1

La position est relativement bien précise du au nombre élevé d'APs présent dans l'étage. De plus, le référentiel étant plus éloigné
(on a un plan "dézoomé"), on a une impression d'une position mieux estimée. Nous avons toutefois remarqué des anomalies où la position semblait s'être brusquement déplacée entre deux affichages de mesures.
Elles semblaient plus extrême que lors de nos expérimentation en salle B30.

Après quelques tests, il semblerait que deux APs soient la limite minimum suffisante pour obtenir une position du smartphone. Toutefois, nous avons constaté que celle-ci semblait être moins précise.
De plus, on appelle pas cela de la bilatération... Pour un positionnement efficace, il conviendrait de fixer la limite minimum à 3 APs.

## Questions 2.2

Oui, c'est tout à fait possible. Cela implique d'avoir un référentiel vertical qui ait du sens, surtout dans un contexte où on souhaite étendre le nombre d'AP à d'autres étages.
Par exemple, l'AP en B34 est défini au niveau 0, mais quid de si un jour on souhait installer des AP à l'étage A ? (Valeur verticale négative ?).
Dans notre app elle-même, il sera impossible d'afficher la hauteur en l'état, il faudrait une modélisation 3D de l'étage par exemple.  