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

