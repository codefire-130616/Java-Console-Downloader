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

/**
 *
 * @author CodeFireUA <edu@codefire.com.ua>
 */
public class DownloadHandler implements DownloaderListener {

    @Override
    public void downloadBegin(DownloaderTask task) {
        // TODO: Burn your code here...
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void downloadProgress(DownloaderTask task) {
        // TODO: Burn your code here...
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void downloadComplete(DownloaderTask task) {
        System.out.printf("File downloaded:\n  %s\n  %s\n\n", task.getSource(), task.getTarget());
    }

}
