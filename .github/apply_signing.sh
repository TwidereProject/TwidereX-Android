cat $SIGNING_KEY | base64 -d > build/key.jks
echo 'storeFile=../build/key.jks
storePassword='+ $KEY_STORE_PASSWORD +
'keyAlias=' + $ALIAS +
'keyPassword=' + $KEY_PASSWORD >build/signing.properties