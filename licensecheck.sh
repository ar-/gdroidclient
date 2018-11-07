#-------------------------------------------------------------------------------
# Copyright (C) 2018 Andreas Redmer <ar-gdroid@abga.be>
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#-------------------------------------------------------------------------------
EXCLUDE="./app/build/generated/*"
# check source
RET=`licensecheck \`find . \( -name "*.java" -or -name "*.properties" -or -name "*.xml"  -or -name "*.sh" \) -not -path "$EXCLUDE"\` | grep -v "GPL (v3 or later)" | grep -v "GENERATED FILE" | wc -l`
if [ $RET -eq 0 ]; then
	echo all licences in directory okay
else
	licensecheck `find . \( -name "*.java" -or -name "*.properties" -or -name "*.xml"  -or -name "*.sh" \) -not -path "$EXCLUDE"` | grep -v "GPL (v3 or later)" | grep -v "GENERATED FILE"
	echo ERROR : there are $RET files with wrong licence - listed above
	exit 1
fi

# check source again with all files in git history
RET=`licensecheck \`git whatchanged --since '01/01/2012' --oneline --name-only --pretty=format: | sort | uniq\` |grep -v "GPL (v3 or later)" | grep -v "GENERATED FILE" | grep -v ".md:" | grep -v COPYING | grep -v ".p:" | wc -l`
if [ $RET -eq 0 ]; then
	echo all licences in git managed files okay
else
	licensecheck `git whatchanged --since '01/01/2012' --oneline --name-only --pretty=format: | sort | uniq` |grep -v "GPL (v3 or later)" | grep -v "GENERATED FILE" | grep -v ".md:" | grep -v COPYING | grep -v ".p:"
	echo ERROR : there are $RET files with wrong licence - listed above
	exit 1
fi

# check years in copyrights, if they were changed this year, they must have this year in the copyright
RET=`licensecheck --copyright \`git whatchanged --since '01/01/2018' --oneline --name-only --pretty=format: | sort | uniq\` | grep -v 2018 | grep -C1 Copyright | wc -l`
if [ $RET -eq 0 ]; then
	echo all years in copyright declarations in git managed files for this year okay
else
	licensecheck --copyright `git whatchanged --since '01/01/2018' --oneline --name-only --pretty=format: | sort | uniq` | grep -v 2018 | grep -C1 Copyright
	echo ERROR : the files above have the wrong year in the copyright declaration, they have been touched this year, so they must contain this year
	exit 1
fi


#check COPYING files
LICSHA=`find . -name "COPYING" -not -path "$EXCLUDE" | xargs sha512sum | egrep -o "[a-f0-9]+ " | sort -u`
LICMD5=`find . -name "COPYING" -not -path "$EXCLUDE" | xargs    md5sum | egrep -o "[a-f0-9]+ " | sort -u`
if [ $LICSHA = "7633623b66b5e686bb94dd96a7cdb5a7e5ee00e87004fab416a5610d59c62badaf512a2e26e34e2455b7ed6b76690d2cd47464836d7d85d78b51d50f7e933d5c" ]; then
	if [ $LICMD5 = "d32239bcb673463ab874e80d47fae504" ]; then
		echo all license files okay
	else
		echo ERROR : MD5 of COPYING file has changed
		exit 1
	fi
else
	echo ERROR : SHA of COPYING file has changed
	exit 1
fi


