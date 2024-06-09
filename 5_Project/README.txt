Le projet contient les fichiers suivants :

firebase
- contient l'implémentation du Firebase Cloud Functions utilisé pour les notifications

app
- contient l'app android


========

L'application android est construit comme suit : 
 
Une activité "MainActivity" qui peut afficher 2 fragments : "LoginFragment", qui peut login, et "ChatFragment" qui est la vue du chat. L'état, les interactions entre les fragment et la vue et la logique métier est gérée dans "ChatViewModel". La navigation entre les fragments est gérée dans "MainActivity".

"ChatFragment" contient un Recycler dont l'adapter et le DiffCallback est dans le dossier recyclerview.

MessagingService est une implémentation d'un service pour pouvoir recevoir les notifications en background.