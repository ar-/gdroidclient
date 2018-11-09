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
package org.gdroid.gdroid.tasks;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * JarReader: JarReader maps all resources included in a
 * Zip or Jar file. Additionaly, it provides a method to extract one
 * as a blob.
 */
public final class JarReader
{

    // external debug flag
    public boolean debugOn=false;

    // jar resource mapping tables
    private Hashtable<String, Integer> htSizes=new Hashtable<String, Integer>();
    private Hashtable<String, byte[]> htJarContents=new Hashtable<String, byte[]>();

    // a jar file
    private String jarFileName;

    /**
     * creates a JarReader. It extracts all resources from a Jar
     * into an internal hashtable, keyed by resource names.
     * @param jarFileName a jar or zip file
     */
    public JarReader(String jarFileName)
    {
        this.jarFileName=jarFileName;
        init();
    }

    /**
     * Extracts a jar resource as a blob.
     * @param name a resource name.
     */
    public byte[] getResource(String name)
    {
        return (byte[])htJarContents.get(name);
    }

    /** initializes internal hash tables with Jar file resources.  */
    private void init()
    {
        try
        {
            // extracts just sizes only.
            ZipFile zf=new ZipFile(jarFileName);
            Enumeration e=zf.entries();
            while (e.hasMoreElements())
            {
                ZipEntry ze=(ZipEntry)e.nextElement();

                if (debugOn)
                {
                    System.out.println(dumpZipEntry(ze));
                }

                htSizes.put(ze.getName(),new Integer((int)ze.getSize()));
            }
            zf.close();

            // extract resources and put them into the hashtable.
            FileInputStream fis=new FileInputStream(jarFileName);
            BufferedInputStream bis=new BufferedInputStream(fis);
            ZipInputStream zis=new ZipInputStream(bis);
            ZipEntry ze=null;
            while ((ze=zis.getNextEntry())!=null)
            {
                if (ze.isDirectory())
                {
                    continue;
                }

                if (debugOn)
                {
                    System.out.println("ze.getName()="+ze.getName()+
                            ","+"getSize()="+ze.getSize() );
                }

                int size=(int)ze.getSize();
                // -1 means unknown size.
                if (size==-1)
                {
                    size=((Integer)htSizes.get(ze.getName())).intValue();
                }

                byte[] b=new byte[(int)size];
                int rb=0;
                int chunk=0;
                while (((int)size - rb) > 0)
                {
                    chunk=zis.read(b,rb,(int)size - rb);
                    if (chunk==-1)
                    {
                        break;
                    }
                    rb+=chunk;
                }

                // add to internal resource hashtable
                htJarContents.put(ze.getName(),b);

                if (debugOn)
                {
                    System.out.println( ze.getName()+"  rb="+rb+
                            ",size="+size+
                            ",csize="+ze.getCompressedSize() );
                }
            }
        }
        catch (NullPointerException e)
        {
            System.out.println("done.");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Dumps a zip entry into a string.
     * @param ze a ZipEntry
     */
    private String dumpZipEntry(ZipEntry ze)
    {
        StringBuffer sb=new StringBuffer();
        if (ze.isDirectory())
        {
            sb.append("d ");
        }
        else
        {
            sb.append("f ");
        }

        if (ze.getMethod()==ZipEntry.STORED)
        {
            sb.append("stored   ");
        }
        else
        {
            sb.append("defalted ");
        }

        sb.append(ze.getName());
        sb.append("\t");
        sb.append(""+ze.getSize());
        if (ze.getMethod()==ZipEntry.DEFLATED)
        {
            sb.append("/"+ze.getCompressedSize());
        }

        return (sb.toString());
    }

    /**
     * Is a test driver. Given a jar file and a resource name, it trys to
     * extract the resource and then tells us whether it could or not.
     *
     * <strong>Example</strong>
     * Let's say you have a JAR file which jarred up a bunch of gif image
     * files. Now, by using JarReader, you could extract, create, and
     * display those images on-the-fly.
     * <pre>
     *     ...
     *     JarReader JR=new JarReader("GifBundle.jar");
     *     Image image=Toolkit.createImage(JR.getResource("logo.gif");
     *     Image logo=Toolkit.getDefaultToolkit().createImage(
     *                   JR.getResources("logo.gif")
     *                   );
     *     ...
     * </pre>
     */
    public static void main(String[] args) throws IOException
    {
        if (args.length!=2)
        {
            System.err.println(
                    "usage: java JarReader <jar file name> <resource name>"
            );
            System.exit(1);
        }

        JarReader jr=new JarReader(args[0]);
        byte[] buff=jr.getResource(args[1]);
        if (buff==null)
        {
            System.out.println("Could not find "+args[1]+".");
        }
        else
        {
            System.out.println("Found "+args[1]+
                    " (length="+buff.length+").");
        }
    }

}	// End of JarReader class.
