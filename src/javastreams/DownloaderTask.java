/*
 * Copyright (C) 2016 CodeFireUA <edu@codefire.com.ua>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javastreams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CodeFireUA <edu@codefire.com.ua>
 */
public class DownloaderTask implements Runnable {
    
    private final Downloader downloader;
    private final File store;
    private final URL source;
    private File target;
    private int bufferSize = 16384;
    private long downloaded;

    public DownloaderTask(Downloader downloader, File store, URL source) {
        this.downloader = downloader;
        this.store = store;
        this.source = source;
    }

    public URL getSource() {
        return source;
    }

    public File getTarget() {
        return target;
    }

    public long getDownloaded() {
        return downloaded;
    }

    @Override
    public void run() {
        try {
            URLConnection conn = source.openConnection();
            conn.getContentType();

            URL targetUrl = conn.getURL();

            long total = conn.getContentLengthLong();

            String sourcefile = new File(targetUrl.getFile()).getName();
            String filename = new String(sourcefile.getBytes("ISO-8859-1"), "UTF-8");
            File targetFile = new File(store, URLDecoder.decode(filename, "UTF-8"));
            
            downloader.downloadBegin(this);

            try (BufferedInputStream bis = new BufferedInputStream(targetUrl.openStream());
                    FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[bufferSize];

                for (int read; (read = bis.read(buffer)) >= 0;) {
                    fos.write(buffer, 0, read);
                    fos.flush();

                    downloaded += read;

                    if (read > 0) {
                        downloader.downloadProgress(this);
                    }
                }
                System.out.println();
            }
            
            downloader.downloadComplete(this);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
