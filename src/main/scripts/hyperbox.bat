@rem
@rem Hyperbox - Virtual Infrastructure Manager
@rem Copyright (C) 2021 Max Dor
@rem
@rem https://apps.kamax.io/hyperbox/
@rem
@rem This program is free software: you can redistribute it and/or modify
@rem it under the terms of the GNU Affero General Public License as published by
@rem the Free Software Foundation, either version 3 of the License, or
@rem (at your option) any later version.
@rem
@rem This program is distributed in the hope that it will be useful,
@rem but WITHOUT ANY WARRANTY; without even the implied warranty of
@rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@rem GNU Affero General Public License for more details.
@rem
@rem You should have received a copy of the GNU Affero General Public License
@rem along with this program.  If not, see <http://www.gnu.org/licenses/>.
@rem

@echo off

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.

set JAVA_OPTS=-splash:"%DIRNAME%\icons\login-header.png"
"%DIRNAME%\bin\hbox-client.bat"
