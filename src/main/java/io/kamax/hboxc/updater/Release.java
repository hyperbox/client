/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Max Dor
 *
 * https://apps.kamax.io/hyperbox
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.hboxc.updater;

import io.kamax.tools.Version;

import java.net.URL;
import java.util.Date;

public class Release implements _Release {

    private String channel;
    private Version version;
    private Date date;
    private URL downloadUrl;
    private URL changelogUrl;

    public Release(String channel, Version version, Date releaseDate, URL downloadUrl, URL changelogUrl) {
        this.channel = channel;
        this.version = version;
        this.downloadUrl = downloadUrl;
        this.changelogUrl = changelogUrl;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public URL getChangeLogURL() {
        return changelogUrl;
    }

    @Override
    public URL getDownloadURL() {
        return downloadUrl;
    }

}
