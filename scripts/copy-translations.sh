#!/bin/bash
sourcedir=../fdroidclient/app/src/main/res/
targetdir=./app/src/main/res/
langs="values values-af values-ast values-be values-bg values-bo values-ca values-cs values-da values-de values-el values-eo values-es values-et values-eu values-fa values-fi values-fr values-gl values-hi values-hr values-hu values-hy values-id values-in values-is values-it values-iw values-ja values-kab values-kn values-ko values-lt values-lv values-mk values-ml values-my values-nb values-nl values-nl-rBE values-pl values-pt-rBR values-pt-rPT values-ro values-ru values-sc values-sk values-sl values-sn values-sq values-sr values-sv values-ta values-te values-th values-tr values-ug values-uk values-ur values-vi values-zh-rCN values-zh-rHK values-zh-rTW"

if [ ! -d $sourcedir ]; then
  echo sourcedir not found
  exit 1
fi
if [ ! -d $targetdir ]; then
  echo targetdir not found
  exit 2
fi

if [[ $# -eq 0 ]] ; then
    echo 'no CLI argument given'
    exit 3
fi

key=$1

for lang in $langs
do
    # init
    #mkdir $targetdir$lang # done once , all good
    # echo "<resources>" > $targetdir/$lang/strings.xml
    # echo "" >> $targetdir/$lang/strings.xml
    # echo "" >> $targetdir/$lang/strings.xml
    # echo "</resources>" >> $targetdir/$lang/strings.xml
    # continue

    echo processing $lang fetching $key
    newcontent=`grep "name=\"${key}\"" $sourcedir/$lang/strings.xml`
    echo new: $newcontent
    #grep '</resources>' ./app/src/main/res/values/strings.xml
    line=$(grep -n '</resources>' $targetdir/$lang/strings.xml | grep -o '^[0-9]*')
    #line=$((line - 1))
    sed -i ${line}"i\\${newcontent}" $targetdir/$lang/strings.xml
    #break
done
