# DMA - Labo 4
Auteurs : Bastian Chollet / Kevin Ferati

# Questions théoriques

## Question 5.1

Pour 2 raisons essentielles: 

1. Un entier à 16 bits prend moins d'espace de stockage qu'un float à 32 bits.

3. Les systèmes embarqués ne disposent pas toujours des ressources matérielles (mémoire, unité de calcul, ...) nécessaires pour traiter des nombre à
virgule flottante. Et mêmes s'ils diposent de telles ressources, cela peut nuire à leur consommation d'énergie. Utiliser
des entiers est une solution possible pour contourner ces contraintes, et pourrait être le choix des concepteurs du périphériques.

## Question 5.2

Le service est `Battery Service`, dont [l'UUID est 0x180F](https://www.bluetooth.com/wp-content/uploads/Files/Specification/HTML/Assigned_Numbers/out/en/Assigned_Numbers.pdf?v=1715804781019). Le service dispose d'une [charactéristique](https://github.com/oesmith/gatt-xml/blob/master/org.bluetooth.service.battery_service.xml), [`Battery level`](https://github.com/oesmith/gatt-xml/blob/master/org.bluetooth.characteristic.battery_level.xml), qui permet d'accéder à un champ `level` en uint8 qui va de 0 à 100 compris permettant de représenter le taux de charge du périphérique. L'UUID de cette charactéristique est `0x2A19`. Selon la description du service, la charactéristique peut être uniquement lue. Éventuellement, il est possible de s'inscrire pour des notifications pour recevoir la charge.