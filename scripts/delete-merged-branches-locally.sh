#!/bin/bash
#-------------------------------------------------------------------------------
# Copyright (C) 2018,2019 Andreas Redmer <ar-gdroid@abga.be>
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
git checkout master || exit 1

git branch --merged | egrep -v "(^\*|master|dev|weblate)" | xargs git branch -d

echo here are the remaining branches:
echo 
git branch
