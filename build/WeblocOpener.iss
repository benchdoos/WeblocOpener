; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "WeblocOpener"
#define MyAppPublisher "Eugene Zrazhevsky"
#define MyAppURL "https://benchdoos.github.io/"
#define MyAppExeName "WeblocOpener.exe"
#define MyAppIconsFile "icons.icl"
#define MyAppSourcePath "Z:\work"
#define MyAppAdditionalPath "Z:\work\build\installer"
#define ImagesPath "Z:\work\build\installer\images"
#define ApplicationVersion GetVersionNumbersString('Z:\work\target\WeblocOpener.exe')
#define ApplicationCopyright GetFileCopyright('Z:\work\target\WeblocOpener.exe')

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{F1300E10-BBB2-4695-AC2F-3D58DC0BC0A6}
AppName={#MyAppName}
AppVersion={#ApplicationVersion}
VersionInfoVersion=1.0
AppCopyright={#ApplicationCopyright}

AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={commonpf}\{#MyAppName}
DefaultGroupName={#MyAppName}
OutputDir={#MyAppSourcePath}\target
OutputBaseFilename=WeblocOpenerSetup
LicenseFile={#MyAppAdditionalPath}\languages\license\License.rtf

SetupIconFile={#ImagesPath}\icon.ico
WizardImageFile={#ImagesPath}\WizardImageFile.bmp
WizardSmallImageFile={#ImagesPath}\WizardSmallImageFile.bmp
UninstallDisplayIcon={app}\{#MyAppIconsFile},2


ArchitecturesInstallIn64BitMode=x64

Compression=lzma
SolidCompression=yes
ChangesAssociations=yes
PrivilegesRequired=admin

ShowLanguageDialog=auto
DisableProgramGroupPage=yes
DisableDirPage=no

;https://github.com/HeliumProject/InnoSetup/blob/master/Examples/CodeExample1.iss
[Code]
function GetJavaMajorVersion(): integer;
var
  TempFile: string;
  ResultCode: Integer;
  S: AnsiString;
  P: Integer;
begin
  Result := 0;

  { execute java -version and redirect output to a temp file }
  TempFile := ExpandConstant('{tmp}\javaversion.txt');
  if (not ExecAsOriginalUser(ExpandConstant('{cmd}'), '/c java -version 2> "' + TempFile + '"', '',SW_HIDE, ewWaitUntilTerminated, ResultCode))
    or (ResultCode <> 0) then
  begin
    Log('Failed to execute java -version');
    exit;
  end;

  { read file into variable S }
  LoadStringFromFile(TempFile, S)
  DeleteFile(TempFile);
  Log(Format('java -version output: ' + #13#10 + '%s', [S]));

  { extract version (between quotes) }
  P := Pos('"', S);
  Delete(S, 1, P);
  P := Pos('"', S);
  SetLength(S, P - 1);
  Log(Format('Extracted version: %s', [S]));

  { extract major }
    if Copy(S, 1, 2) = '1.' then
    begin
    Delete(S, 1, 2)
    end;

    P := Pos('.', S);
    Log(Format('Dot position: %d', [P]));
    if (P <> 0) then
    begin
        SetLength(S, P - 1);
    end;

    Log(Format('Major version: %s', [S]));

  Result := StrToIntDef(S, 0);
end;

function InitializeSetup(): boolean;
var
  ResultCode: Integer;
begin
  Log('InitializeSetup called');
  if GetJavaMajorVersion < 17 then
    begin
      if MsgBox(ExpandConstant('{cm:Warning}' #13#13 '{cm:Java17InstallWarning}'), mbConfirmation, MB_YESNO) = idYes then
        begin
          Result := false;
          ShellExec('open', 'https://adoptium.net/marketplace/?arch=any&package=jre&version=17', '', '', SW_SHOWNORMAL, ewNoWait, ResultCode);
        end;
    end
  else
    begin
      Result := true;
    end;
end;


[Registry]
;----File association---------
;.webloc
Root: HKCR; Subkey: ".webloc"; ValueType: string; ValueName: ""; ValueData: "Webloc"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc"; ValueType: string; ValueName: ""; ValueData: {cm:WeblocLink}; Flags: uninsdeletekey
Root: HKCR; Subkey: "Webloc\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppIconsFile},3"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: uninsdeletevalue
;.webrachive
Root: HKCR; Subkey: ".webarchive"; ValueType: string; ValueName: ""; ValueData: "Webarchive"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webarchive"; ValueType: string; ValueName: ""; ValueData: {cm:Webarchive}; Flags: uninsdeletekey
Root: HKCR; Subkey: "Webarchive\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppIconsFile},8"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webarchive\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: uninsdeletevalue
;.desktop
Root: HKCR; Subkey: ".desktop"; ValueType: string; ValueName: ""; ValueData: "Desktop"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop"; ValueType: string; ValueName: ""; ValueData: {cm:Desktop}; Flags: uninsdeletekey
Root: HKCR; Subkey: "Desktop\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppIconsFile},9"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: uninsdeletevalue
;---/File association---------

;----Create new file----------
;webloc
Root: HKCR; Subkey: ".webloc\ShellNew"; ValueType: string; ValueName: "ItemName"; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".webloc\ShellNew"; ValueType: string; ValueName: "FileName"; ValueData: "{app}\Template.webloc"; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".webloc\ShellNew"; ValueType: string; ValueName: "NullFile"; ValueData: ""; Flags: uninsdeletevalue

;desktop
Root: HKCR; Subkey: ".desktop\ShellNew"; ValueType: string; ValueName: "ItemName"; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".desktop\ShellNew"; ValueType: string; ValueName: "FileName"; ValueData: "{app}\Template.desktop"; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".desktop\ShellNew"; ValueType: string; ValueName: "NullFile"; ValueData: ""; Flags: uninsdeletevalue
;---/Create new file----------

;----Add edit file menu-------
; webloc
Root: HKCR; Subkey: "Webloc\shell\edit"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\edit\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-edit"" ""%1"" "; Flags: uninsdeletevalue
; desktop
Root: HKCR; Subkey: "Desktop\shell\edit"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\edit\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-edit"" ""%1"" "; Flags: uninsdeletevalue
; url - not working: permission denied
;Root: HKCR; Subkey: "InternetShortcut\shell\edit"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
;Root: HKCR; Subkey: "InternetShortcut\shell\edit\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-edit"" ""%1"" "; Flags: uninsdeletevalue
;---/Add edit file menu-------

;----Add qr file menu---------
; webloc
Root: HKCR; Subkey: "Webloc\shell\GenerateQRCode"; ValueType: string; ValueName: ""; ValueData: {cm:GenerateQrCode}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\GenerateQRCode"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\GenerateQRCode\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-qr"" ""%1"" "; Flags: uninsdeletevalue
; desktop
Root: HKCR; Subkey: "Desktop\shell\GenerateQRCode"; ValueType: string; ValueName: ""; ValueData: {cm:GenerateQrCode}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\GenerateQRCode"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\GenerateQRCode\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-qr"" ""%1"" "; Flags: uninsdeletevalue
;---/Add qr file menu---------

;----Add copy qr file menu-------
;webloc
Root: HKCR; Subkey: "Webloc\shell\CopyQRCode"; ValueType: string; ValueName: ""; ValueData: {cm:CopyQRMenu}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\CopyQRCode"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\CopyQRCode\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-copy-qr"" ""%1"" "; Flags: uninsdeletevalue
;desktop
Root: HKCR; Subkey: "Desktop\shell\CopyQRCode"; ValueType: string; ValueName: ""; ValueData: {cm:CopyQRMenu}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\CopyQRCode"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\CopyQRCode\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-copy-qr"" ""%1"" "; Flags: uninsdeletevalue
;---/Add copy qr file menu-------

;----Add copy file menu----------
;webloc
Root: HKCR; Subkey: "Webloc\shell\Copy"; ValueType: string; ValueName: ""; ValueData: {cm:CopyMenu}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\Copy"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\Copy\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-copy"" ""%1"" "; Flags: uninsdeletevalue
;desktop
Root: HKCR; Subkey: "Desktop\shell\Copy"; ValueType: string; ValueName: ""; ValueData: {cm:CopyMenu}; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\Copy"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\Copy\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-copy"" ""%1"" "; Flags: uninsdeletevalue
;----Add copy file menu----------

;----Add open in browser submenu---
Root: HKCR; Subkey: "Webloc\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "MUIVerb"; ValueData: "{cm:OpenInBrowserMenu}"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webloc\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "SubCommands"; ValueData: "wo.OpenInChrome;wo.OpenInFireFox;wo.OpenInEdge;wo.OpenInOpera;wo.OpenInYandex;wo.OpenInVivaldi;wo.OpenInChromePrivate;wo.OpenInFireFoxPrivate;wo.OpenInEdgePrivate;wo.OpenInOperaPrivate;wo.OpenInYandexPrivate;wo.OpenInVivaldiPrivate"; Flags: uninsdeletevalue

Root: HKCR; Subkey: "Webarchive\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "MUIVerb"; ValueData: "{cm:OpenInBrowserMenu}"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webarchive\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Webarchive\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "SubCommands"; ValueData: "wo.OpenInChrome;wo.OpenInFireFox;wo.OpenInEdge;wo.OpenInOpera;wo.OpenInYandex;wo.OpenInVivaldi;wo.OpenInChromePrivate;wo.OpenInFireFoxPrivate;wo.OpenInEdgePrivate;wo.OpenInOperaPrivate;wo.OpenInYandexPrivate;wo.OpenInVivaldiPrivate"; Flags: uninsdeletevalue

Root: HKCR; Subkey: "Desktop\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "MUIVerb"; ValueData: "{cm:OpenInBrowserMenu}"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "icon"; ValueData: """{app}\{#MyAppIconsFile}"",2"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Desktop\shell\OpenInBrowserMenu"; ValueType: string; ValueName: "SubCommands"; ValueData: "wo.OpenInChrome;wo.OpenInFireFox;wo.OpenInEdge;wo.OpenInOpera;wo.OpenInYandex;wo.OpenInVivaldi;wo.OpenInChromePrivate;wo.OpenInFireFoxPrivate;wo.OpenInEdgePrivate;wo.OpenInOperaPrivate;wo.OpenInYandexPrivate;wo.OpenInVivaldiPrivate"; Flags: uninsdeletevalue

;Adding select browser
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInChrome"; ValueType: string; ValueName: ""; ValueData: "Google Chrome"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInChrome\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""chrome"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInFireFox"; ValueType: string; ValueName: ""; ValueData: "Mozilla Firefox"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInFireFox\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""firefox"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInEdge"; ValueType: string; ValueName: ""; ValueData: "Microsoft Edge"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInEdge\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""edge"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInOpera"; ValueType: string; ValueName: ""; ValueData: "Opera"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInOpera\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""opera"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInYandex"; ValueType: string; ValueName: ""; ValueData: "Yandex browser"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInYandex\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""yandex"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInVivaldi"; ValueType: string; ValueName: ""; ValueData: "Vivaldi"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInVivaldi\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""vivaldi"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInBrave"; ValueType: string; ValueName: ""; ValueData: "Brave"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInBrave\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""brave"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInDuckDuckGo"; ValueType: string; ValueName: ""; ValueData: "DuckDuckGo"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInDuckDuckGo\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""duckduckgo"" ""%1"" "; Flags: uninsdeletevalue


;Adding select browser private
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInChromePrivate"; ValueType: string; ValueName: ""; ValueData: "Incognito Google Chrome"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInChromePrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""chrome-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInFireFoxPrivate"; ValueType: string; ValueName: ""; ValueData: "Incognito Mozilla Firefox"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInFireFoxPrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""firefox-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInEdgePrivate"; ValueType: string; ValueName: ""; ValueData: "InPrivate Microsoft Edge"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInEdgePrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""edge-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInOperaPrivate"; ValueType: string; ValueName: ""; ValueData: "Private Opera"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInOperaPrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""opera-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInYandexPrivate"; ValueType: string; ValueName: ""; ValueData: "Incognito Yandex browser"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInYandexPrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""yandex-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInVivaldiPrivate"; ValueType: string; ValueName: ""; ValueData: "Private Vivaldi"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInVivaldiPrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""vivaldi-private"" ""%1"" "; Flags: uninsdeletevalue

Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInBravePrivate"; ValueType: string; ValueName: ""; ValueData: "InPrivate Brave"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\CommandStore\shell\wo.OpenInBravePrivate\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""-open-browser"" ""brave-private"" ""%1"" "; Flags: uninsdeletevalue


;---/Add open in browser submenu---
; Add updater autorun
Root: HKCU; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "Update"; ValueData: """start weblocopener -update-silent"""; Flags: uninsdeletevalue

; Add app settings
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: ""; ValueData: ""; Flags: uninsdeletevalue
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "auto_update_enabled"; ValueData: "true"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "browser"; ValueData: "default"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "open_folder_for_qr"; ValueData: "true"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "notifications"; ValueData: "true"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "converter_export_extension"; ValueData: "url"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "dark_mode"; ValueData: "{{""type"":""SYSTEM""}}"; Flags: uninsdeletevalue createvalueifdoesntexist
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "locale"; ValueData: "default"; Flags: uninsdeletevalue createvalueifdoesntexist

Root: HKCU; Subkey: "SOFTWARE\{#MyAppName}\Capabilities"; ValueType: string; ValueName: "ApplicationDescription"; ValueData: "Open, edit and create .webloc links on Windows"; Flags: uninsdeletevalue
Root: HKCU; Subkey: "SOFTWARE\{#MyAppName}\Capabilities\FileAssociations"; ValueType: string; ValueName: ".webloc"; ValueData: {cm:WeblocLink}; Flags: uninsdeletevalue

; Adds start command to cmd
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\{#MyAppName}.exe"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName}"; Flags: uninsdeletevalue
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\{#MyAppName}.exe"; ValueType: string; ValueName: "Path"; ValueData: "{app}\"; Flags: uninsdeletevalue


[Registry]
;delete old entries
;Root: HKCU; Subkey: "<path>"; ValueName: "<Value>"; ValueType: none; Flags: deletevalue;
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "current_version"; ValueData: none; Flags: deletevalue;
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "install_location"; ValueData: none; Flags: deletevalue;
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "name"; ValueData: none; Flags: deletevalue;
Root: HKCU; Subkey: "SOFTWARE\JavaSoft\Prefs\{#MyAppName}"; ValueType: string; ValueName: "url_update_info"; ValueData: none; Flags: deletevalue;

[Languages]
Name: "English"; MessagesFile: "{#MyAppAdditionalPath}\languages\English.isl";
Name: "German"; MessagesFile: "{#MyAppAdditionalPath}\languages\German.isl"
Name: "French"; MessagesFile: "{#MyAppAdditionalPath}\languages\French.isl"
Name: "Italian"; MessagesFile: "{#MyAppAdditionalPath}\languages\Italian.isl"
Name: "Russian"; MessagesFile: "{#MyAppAdditionalPath}\languages\Russian.isl"


[Files]
Source: "{#MyAppSourcePath}\target\WeblocOpener.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#MyAppSourcePath}\target\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs
Source: "{#MyAppSourcePath}\build\Template.webloc"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#MyAppSourcePath}\build\Template.desktop"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#ImagesPath}\icons.icl"; DestDir: "{app}"; Flags: ignoreversion

; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[InstallDelete]
;Deletes old files
Type: files; Name: "{group}\{#MyAppName} {cm:Update}.lnk";
Type: files; Name: "{commonprograms}\{#MyAppName} {cm:Update}.lnk"
Type: files; Name: "{app}\Updater.jar"
Type: files; Name: "{app}\readme.rtf"
;Type: filesandordirs; Name: "{app}\lib"

Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\{#MyAppName}\DEBUG"
Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\{#MyAppName}\INFO"
Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\{#MyAppName}\WARN"

Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\Updater\DEBUG"
Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\Updater\INFO"
Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Log\Updater\WARN"

Type: filesandordirs; Name: "{%TEMP}\{#MyAppName}\Updater_.jar"


[Icons]
Name: "{group}\{#MyAppName} {cm:Settings}"; Filename: "{app}\{#MyAppExeName}"; OnlyBelowVersion: 6.1.9;
Name: "{commonprograms}\{#MyAppName} {cm:Settings}"; Filename: "{app}\{#MyAppExeName}";  MinVersion: 6.2.9200; 

;Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"; IconFilename: "{app}\{#MyAppIconsFile}"; IconIndex: 1;  Components: main

;--------------------------Windows task----------------------
[Run]
Filename: "schtasks"; \
    Parameters: "/Create /F /SC DAILY /TN ""CheckUpdatesWeblocOpener"" /TR ""'{app}\{#MyAppExeName}' -update-silent"; \
    Flags: runhidden

[UninstallRun]
Filename: "schtasks"; \
    Parameters: "/delete /tn CheckUpdatesWeblocOpener /f"; \
    Flags: runhidden
;------------------------/Windows task-----------------------

[Run]
Filename: https://benchdoos.github.io/; Description: "{cm:ProgramOnTheWeb,{#MyAppName}}"; Flags: postinstall shellexec  unchecked
Filename: {app}\{#MyAppExeName}; Parameters: "-welcome"; Description: {cm:ShowWelcomePage}; Flags: nowait postinstall
