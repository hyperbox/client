/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013-2015 Maxime Dor
 * 
 * http://kamax.io/hbox/
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

!define VERSION "@CLIENT_VERSION@"
Name "Hyperbox Client"
OutFile "@CLIENT_INSTALLER_OUTPUT@"
InstallDir "@CLIENT_INSTALL_DIR@"
RequestExecutionLevel admin

Page directory
Page components
Page instfiles

Section "Core files"
SetOutPath $INSTDIR
RMDir /r "$INSTDIR\bin"
RMDir /r "$INSTDIR\lib"
File /r "@CLIENT_OUT_BIN_DIR@\*.*"
WriteUninstaller $INSTDIR\uninstaller.exe
SectionEnd

Section "Start Menu Shortcuts"
CreateDirectory "$SMPROGRAMS\Hyperbox\Client"
CreateShortCut "$SMPROGRAMS\Hyperbox\Client\Hyperbox.lnk" "$INSTDIR\hyperbox.exe"
CreateShortCut "$SMPROGRAMS\Hyperbox\Client\Uninstall.lnk" "$INSTDIR\uninstaller.exe"
SectionEnd

Section "Uninstall"
RMDir /r "$INSTDIR"
RMDir /r "$SMPROGRAMS\Hyperbox\Client"
RMDir "$SMPROGRAMS\Hyperbox"
SectionEnd