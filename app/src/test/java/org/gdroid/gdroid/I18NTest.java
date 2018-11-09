/*
 * Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
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
 *
 */

package org.gdroid.gdroid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class I18NTest {


    @Test
    public void try_testConfig() {
        String [] userLocales = new String[]{"pl_PL","de_DE","en_AU","es_AR"};

        // these are not the app-languages, as they can be incomplete, it is the language of a particular ressource (string)
        String [] resLocales = new String[]{"en","de","en-AU","en-GB","pl"};

        final String locale = Util.getUsableLocale(userLocales, resLocales);

        assertEquals("pl", locale);

    }

    // table 2 on https://developer.android.com/guide/topics/resources/multilingual-support
    @Test
    public void try_exampleTable2() {
        String [] userLocales = new String[]{"fr_CH"};

        // these are not the app-languages, as they can be incomplete, it is the language of a particular ressource (string)
        String [] resLocales = new String[]{"en","de_DE","es_ES","fr_FR","it_IT"};

        final String locale = Util.getUsableLocale(userLocales, resLocales);

        assertEquals("fr_FR", locale);

    }

    // table 3 on https://developer.android.com/guide/topics/resources/multilingual-support
    @Test
    public void try_exampleTable3() {
        String [] userLocales = new String[]{"fr_CH","it_CH"};

        // these are not the app-languages, as they can be incomplete, it is the language of a particular ressource (string)
        String [] resLocales = new String[]{"en","de_DE","es_ES","it_IT"};

        final String locale = Util.getUsableLocale(userLocales, resLocales);

        assertEquals("it_IT", locale);
    }

    // table 3 on https://developer.android.com/guide/topics/resources/multilingual-support
    @Test
    public void try_Fallback() {
        String [] userLocales = new String[]{"fr_CH","it_CH"};

        // these are not the app-languages, as they can be incomplete, it is the language of a particular ressource (string)
        String [] resLocales = new String[]{"en","es_ES","pt_BR"};

        final String locale = Util.getUsableLocale(userLocales, resLocales);

        assertEquals("en", locale);
    }
}
