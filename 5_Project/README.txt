Le projet contient les dossiers suivants :

firebase
- contient l'implémentation du Firebase Cloud Functions utilisé pour les notifications

app
- contient l'app android


========

L'application android est construit comme suit : 
 
Une activité "MainActivity" qui peut afficher 2 fragments : "LoginFragment", qui peut login, et "ChatFragment" qui est la vue du chat. L'état, les interactions entre les fragment et la vue et la logique métier est gérée dans "ChatViewModel". La navigation entre les fragments est gérée dans "MainActivity".

"ChatFragment" contient un Recycler dont l'adapter et le DiffCallback est dans le dossier recyclerview.

MessagingService est une implémentation d'un service pour pouvoir recevoir les notifications en background.


=======

Voici les règles de gestion de controle d'accès dans la DB :

{
  "rules": {
    "users" : {
    ".read": "true",  // 2024-6-22
    ".write": "true",  // 2024-6-22
    },
      
    "messages-admin" : {
      ".write" : "auth !== null && root.child('users').child(auth.uid).child('admin').val() === true",
      ".read"  : "auth !== null && root.child('users').child(auth.uid).child('admin').val() === true"
    },
      
		"messages" : {
      "$id" : {
        ".write": "auth !== null 
        			&& (newData.exists()
                  || root.child('users').child(auth.uid).child('admin').val() === true
                  || root.child('users').child(auth.uid).child('author').val() === data.child('author').val())"
      },

      ".read" : "true"
    }
  }
}