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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CodeFireUA <edu@codefire.com.ua>
 */
public class Downloader {

    private final URL fileList;
    private final List<URL> files;
    private final File store;
    private int bufferSize = 524288;
    
    private List<DownloaderListener> listeners;

    public Downloader(URL fileList, File store) {
        this.fileList = fileList;
        this.store = store;
        this.files = new ArrayList<>();
        this.listeners = Collections.synchronizedList(new ArrayList<DownloaderListener>());
    }

    public boolean add(DownloaderListener listener) {
        return listeners.add(listener);
    }

    public boolean remove(DownloaderListener listener) {
        return listeners.remove(listener);
    }

    public void download() {
        retrieveFiles();

        for (URL fileUrl : files) {
//            System.out.println("Download: " + fileUrl);
            downloadUrl(fileUrl);
        }
    }

    private void retrieveFiles() {
        try (Scanner scanner = new Scanner(fileList.openStream())) {
            while (scanner.hasNextLine()) {
                URL fileUrl = new URL(scanner.nextLine());
                files.add(fileUrl);
            }
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void downloadUrl(URL sourceUrl) {
        try {
            URLConnection conn = sourceUrl.openConnection();
            conn.getContentType();
            
            URL targetUrl = conn.getURL();
            
            long total = conn.getContentLengthLong();
            long downloaded = 0;

            String sourcefile = new File(targetUrl.getFile()).getName();
            String filename = new String(sourcefile.getBytes("ISO-8859-1"), "UTF-8");
            File targetFile = new File(store, URLDecoder.decode(filename, "UTF-8"));

            try (BufferedInputStream bis = new BufferedInputStream(targetUrl.openStream());
                    FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[bufferSize];

                for (int read; (read = bis.read(buffer)) >= 0;) {
                    fos.write(buffer, 0, read);
                    fos.flush();

                    downloaded += read;

                    if (read > 0) {
                        // [===============================>   ] 100.00%
//                        String progress = "===>";
//                        
//                        System.out.printf("\r[%-70s] %6.02f%%", progress, (double) downloaded * 100. / total);
                    }
                }
                System.out.println();
            }
            
            for (DownloaderListener listener : listeners) {
                listener.downloadComplete(sourceUrl, targetFile);
            }
            
//            System.out.println("Downloaded: " + targetFile);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
