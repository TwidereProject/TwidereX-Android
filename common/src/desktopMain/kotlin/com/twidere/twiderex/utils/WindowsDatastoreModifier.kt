/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.utils

import javassist.ClassPool

object WindowsDatastoreModifier {
    fun ensureWindowsDatastore() {
        val pool = ClassPool.getDefault()
        val dataStore = pool.getCtClass("androidx.datastore.core.SingleProcessDataStore")
        val writeData = dataStore.getDeclaredMethod("writeData\$datastore_core")
        val unCloseableStream = "androidx.datastore.core.SingleProcessDataStore\$UncloseableOutputStream"
        writeData.setBody(
            """
       { 
          java.io.File file = getFile();
          this.createParentDirectories(file);
          java.io.File scratchFile = new java.io.File(file.getAbsolutePath() + ".temp");
          try {
             java.io.Closeable var3 = (java.io.Closeable)(new java.io.FileOutputStream(scratchFile));
             boolean var4 = false;
             boolean var5 = false;
             java.lang.Throwable var14 = null;
    
             try {
                java.io.FileOutputStream stream = (java.io.FileOutputStream)var3;
                java.io.OutputStream unCloseableStream = new $unCloseableStream(stream);
                serializer.writeTo($1, unCloseableStream, $2);
                int var7 = false;
                stream.getFD().sync();
             } catch (Throwable var11) {
                var14 = var11;
                throw var11;
             } finally {
                kotlin.io.CloseableKt.closeFinally(var3, var14);
             }
             java.io.File bakFile = new java.io.File(file.getAbsolutePath() + ".bak");
             file.renameTo(bakFile);
             if (!scratchFile.renameTo(file)) {
                bakFile.renameTo(file);
                throw (Throwable)(new java.io.IOException("Unable to rename " + scratchFile + '.' + "This likely means that there are multiple instances of DataStore " + "for this file. Ensure that you are only creating a single instance of " + "datastore for this file."));
             }
             bakFile.delete();
          } catch (java.io.IOException var13) {
             if (scratchFile.exists()) {
                scratchFile.delete();
             }
             throw var13;
          }
          return null;
      }
    """
        )
        dataStore.toClass()
        dataStore.detach()
    }
}
