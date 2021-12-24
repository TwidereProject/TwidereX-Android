echo $SIGNING_KEY | base64 -d > key.jks
echo $GOOGLE_SERVICES | base64 -d > android/google-services.json
echo "storeFile=key.jks
storePassword=$KEY_STORE_PASSWORD
keyAlias=$ALIAS
keyPassword=$KEY_PASSWORD" >signing.properties