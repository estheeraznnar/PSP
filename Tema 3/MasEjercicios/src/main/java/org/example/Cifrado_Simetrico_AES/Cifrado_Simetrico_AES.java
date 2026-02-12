package org.example.Cifrado_Simetrico_AES;

/*Enunciado: Cifrado simétrico de ficheros con AES
Tenemos una aplicación que consta de las clases FileCrypto, KeyManager y MainCrypto.

FileCrypto: Clase que implementa:
    encryptFile(File input, File output) → cifra un fichero con AES/CBC/PKCS5Padding.
    decryptFile(File input, File output) → descifra un fichero cifrado.

KeyManager: Proporciona una clave AES fija de 16 bytes.
MainCrypto: Menú por consola:
    Cifrar fichero
    Descifrar fichero
    Salir

Tareas:

    Implementa KeyManager.getKey() que devuelva SecretKeySpec AES de 16 bytes fijos. (2 pts)
    Implementa FileCrypto.encryptFile() usando CipherOutputStream. (3 pts)
    Implementa FileCrypto.decryptFile() usando CipherInputStream. (3 pts)
    Implementa el menú de MainCrypto y comprueba que cifrar/descifrar recupera el fichero original. (2 pts)

*/
public class Cifrado_Simetrico_AES {
}
