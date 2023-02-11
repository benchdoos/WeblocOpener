; *** Inno Setup version 5.5.3+ French messages ***
;
; To download user-contributed translations of this file, go to:
;   http://www.jrsoftware.org/files/istrans/
;
; Note: When translating this text, do not add periods (.) to the end of
; messages that didn't have them already, because on those messages Inno
; Setup adds the periods automatically (appending a period would result in
; two periods being displayed).
;
; Maintained by Pierre Yager (pierre@levosgien.net)
;
; Contributors : Fr�d�ric Bonduelle, Francis Pallini, Lumina, Pascal Peyrot
;
; Changes :
; + Accents on uppercase letters
;      http://www.academie-francaise.fr/langue/questions.html#accentuation (lumina)
; + Typography quotes [see ISBN: 978-2-7433-0482-9]
;      http://fr.wikipedia.org/wiki/Guillemet (lumina)
; + Binary units (Kio, Mio) [IEC 80000-13:2008]
;      http://fr.wikipedia.org/wiki/Octet (lumina)
; + Reverted to standard units (Ko, Mo) to follow Windows Explorer Standard
;      http://blogs.msdn.com/b/oldnewthing/archive/2009/06/11/9725386.aspx
; + Use more standard verbs for click and retry
;     "click": "Clicker" instead of "Appuyer" 
;     "retry": "Recommencer" au lieu de "R�essayer"

[LangOptions]
; The following three entries are very important. Be sure to read and 
; understand the '[LangOptions] section' topic in the help file.
LanguageName=Fran<00E7>ais
LanguageID=$040C
LanguageCodePage=1252
; If the language you are translating to requires special font faces or
; sizes, uncomment any of the following entries and change them accordingly.
;DialogFontName=
;DialogFontSize=8
;WelcomeFontName=Verdana
;WelcomeFontSize=12
;TitleFontName=Arial
;TitleFontSize=29
;CopyrightFontName=Arial
;CopyrightFontSize=8

[Messages]

; *** Application titles
SetupAppTitle=Installation
SetupWindowTitle=Installation - %1
UninstallAppTitle=D�sinstallation
UninstallAppFullTitle=D�sinstallation - %1

; *** Misc. common
InformationTitle=Information
ConfirmTitle=Confirmation
ErrorTitle=Erreur

; *** SetupLdr messages
SetupLdrStartupMessage=Cet assistant va installer %1. Voulez-vous continuer ?
LdrCannotCreateTemp=Impossible de cr�er un fichier temporaire. Abandon de l'installation
LdrCannotExecTemp=Impossible d'ex�cuter un fichier depuis le dossier temporaire. Abandon de l'installation

; *** Startup error messages
LastErrorMessage=%1.%n%nErreur %2 : %3
SetupFileMissing=Le fichier %1 est absent du dossier d'installation. Veuillez corriger le probl�me ou vous procurer une nouvelle copie du programme.
SetupFileCorrupt=Les fichiers d'installation sont alt�r�s. Veuillez vous procurer une nouvelle copie du programme.
SetupFileCorruptOrWrongVer=Les fichiers d'installation sont alt�r�s ou ne sont pas compatibles avec cette version de l'assistant d'installation. Veuillez corriger le probl�me ou vous procurer une nouvelle copie du programme.
InvalidParameter=Un param�tre non valide a �t� pass� � la ligne de commande :%n%n%1
SetupAlreadyRunning=L'assistant d'installation est d�j� en cours d'ex�cution.
WindowsVersionNotSupported=Ce programme n'est pas pr�vu pour fonctionner avec la version de Windows utilis�e sur votre ordinateur.
WindowsServicePackRequired=Ce programme a besoin de %1 Service Pack %2 ou d'une version plus r�cente.
NotOnThisPlatform=Ce programme ne fonctionne pas sous %1.
OnlyOnThisPlatform=Ce programme ne peut fonctionner que sous %1.
OnlyOnTheseArchitectures=Ce programme ne peut �tre install� que sur des versions de Windows qui supportent ces architectures : %n%n%1
MissingWOW64APIs=La version de Windows que vous utilisez ne dispose pas des fonctionnalit�s n�cessaires pour que l'assistant puisse r�aliser une installation 64 bits. Pour corriger ce probl�me vous devez installer le Service Pack %1.
WinVersionTooLowError=Ce programme requiert la version %2 ou sup�rieure de %1.
WinVersionTooHighError=Ce programme ne peut pas �tre install� sous %1 version %2 ou sup�rieure.
AdminPrivilegesRequired=Vous devez disposer des droits d'administration de cet ordinateur pour installer ce programme.
PowerUserPrivilegesRequired=Vous devez disposer des droits d'administration ou faire partie du groupe � Utilisateurs avec pouvoir � de cet ordinateur pour installer ce programme.
SetupAppRunningError=L'assistant d'installation a d�tect� que %1 est actuellement en cours d'ex�cution.%n%nVeuillez fermer toutes les instances de cette application puis cliquer sur OK pour continuer, ou bien cliquer sur Annuler pour abandonner l'installation.
UninstallAppRunningError=La proc�dure de d�sinstallation a d�tect� que %1 est actuellement en cours d'ex�cution.%n%nVeuillez fermer toutes les instances de cette application  puis cliquer sur OK pour continuer, ou bien cliquer sur Annuler pour abandonner la d�sinstallation.

; *** Misc. errors
ErrorCreatingDir=L'assistant d'installation n'a pas pu cr�er le dossier "%1"
ErrorTooManyFilesInDir=L'assistant d'installation n'a pas pu cr�er un fichier dans le dossier "%1" car celui-ci contient trop de fichiers

; *** Setup common messages
ExitSetupTitle=Quitter l'installation
ExitSetupMessage=L'installation n'est pas termin�e. Si vous abandonnez maintenant, le programme ne sera pas install�.%n%nVous devrez relancer cet assistant pour finir l'installation.%n%nVoulez-vous quand m�me quitter l'assistant d'installation ?
AboutSetupMenuItem=&� propos...
AboutSetupTitle=� Propos de l'assistant d'installation
AboutSetupMessage=%1 version %2%n%3%n%nPage d'accueil de %1 :%n%4
AboutSetupNote=
TranslatorNote=Traduction fran�aise maintenue par Pierre Yager (pierre@levosgien.net)

; *** Buttons
ButtonBack=< &Pr�c�dent
ButtonNext=&Suivant >
ButtonInstall=&Installer
ButtonOK=OK
ButtonCancel=Annuler
ButtonYes=&Oui
ButtonYesToAll=Oui pour &tout
ButtonNo=&Non
ButtonNoToAll=N&on pour tout
ButtonFinish=&Terminer
ButtonBrowse=Pa&rcourir...
ButtonWizardBrowse=Pa&rcourir...
ButtonNewFolder=Nouveau &dossier

; *** "Select Language" dialog messages
SelectLanguageTitle=Langue de l'assistant d'installation
SelectLanguageLabel=Veuillez s�lectionner la langue qui sera utilis�e par l'assistant d'installation :

; *** Common wizard text
ClickNext=Cliquez sur Suivant pour continuer ou sur Annuler pour abandonner l'installation.
BeveledLabel=
BrowseDialogTitle=Parcourir les dossiers
BrowseDialogLabel=Veuillez choisir un dossier de destination, puis cliquez sur OK.
NewFolderName=Nouveau dossier

; *** "Welcome" wizard page
WelcomeLabel1=Bienvenue dans l'assistant d'installation de [name]
WelcomeLabel2=Cet assistant va vous guider dans l'installation de [name/ver] sur votre ordinateur.%n%nIl est recommand� de fermer toutes les applications actives avant de continuer.

; *** "Password" wizard page
WizardPassword=Mot de passe
PasswordLabel1=Cette installation est prot�g�e par un mot de passe.
PasswordLabel3=Veuillez saisir le mot de passe (attention � la distinction entre majuscules et minuscules) puis cliquez sur Suivant pour continuer.
PasswordEditLabel=&Mot de passe :
IncorrectPassword=Le mot de passe saisi n'est pas valide. Veuillez essayer � nouveau.

; *** "License Agreement" wizard page
WizardLicense=Accord de licence
LicenseLabel=Les informations suivantes sont importantes. Veuillez les lire avant de continuer.
LicenseLabel3=Veuillez lire le contrat de licence suivant. Vous devez en accepter tous les termes avant de continuer l'installation.
LicenseAccepted=Je comprends et j'&accepte les termes du contrat de licence
LicenseNotAccepted=Je &refuse les termes du contrat de licence

; *** "Information" wizard pages
WizardInfoBefore=Information
InfoBeforeLabel=Les informations suivantes sont importantes. Veuillez les lire avant de continuer.
InfoBeforeClickLabel=Lorsque vous �tes pr�t � continuer, cliquez sur Suivant.
WizardInfoAfter=Information
InfoAfterLabel=Les informations suivantes sont importantes. Veuillez les lire avant de continuer.
InfoAfterClickLabel=Lorsque vous �tes pr�t � continuer, cliquez sur Suivant.

; *** "User Information" wizard page
WizardUserInfo=Informations sur l'Utilisateur
UserInfoDesc=Veuillez saisir les informations qui vous concernent.
UserInfoName=&Nom d'utilisateur :
UserInfoOrg=&Organisation :
UserInfoSerial=Num�ro de &s�rie :
UserInfoNameRequired=Vous devez au moins saisir un nom.

; *** "Select Destination Location" wizard page
WizardSelectDir=Dossier de destination
SelectDirDesc=O� [name] doit-il �tre install� ?
SelectDirLabel3=L'assistant va installer [name] dans le dossier suivant.
SelectDirBrowseLabel=Pour continuer, cliquez sur Suivant. Si vous souhaitez choisir un dossier diff�rent, cliquez sur Parcourir.
DiskSpaceMBLabel=Le programme requiert au moins [mb] Mo d'espace disque disponible.
CannotInstallToNetworkDrive=L'assistant ne peut pas installer sur un disque r�seau.
CannotInstallToUNCPath=L'assistant ne peut pas installer sur un chemin UNC.
InvalidPath=Vous devez saisir un chemin complet avec sa lettre de lecteur ; par exemple :%n%nC:\APP%n%nou un chemin r�seau de la forme :%n%n\\serveur\partage
InvalidDrive=L'unit� ou l'emplacement r�seau que vous avez s�lectionn� n'existe pas ou n'est pas accessible. Veuillez choisir une autre destination.
DiskSpaceWarningTitle=Espace disponible insuffisant
DiskSpaceWarning=L'assistant a besoin d'au moins %1 Ko d'espace disponible pour effectuer l'installation, mais l'unit� que vous avez s�lectionn�e ne dispose que de %2 Ko d'espace disponible.%n%nSouhaitez-vous continuer malgr� tout ?
DirNameTooLong=Le nom ou le chemin du dossier est trop long.
InvalidDirName=Le nom du dossier est invalide.
BadDirName32=Le nom du dossier ne doit contenir aucun des caract�res suivants :%n%n%1
DirExistsTitle=Dossier existant
DirExists=Le dossier :%n%n%1%n%nexiste d�j�. Souhaitez-vous installer dans ce dossier malgr� tout ?
DirDoesntExistTitle=Le dossier n'existe pas
DirDoesntExist=Le dossier %n%n%1%n%nn'existe pas. Souhaitez-vous que ce dossier soit cr�� ?

; *** "Select Components" wizard page
WizardSelectComponents=Composants � installer
SelectComponentsDesc=Quels composants de l'application souhaitez-vous installer ?
SelectComponentsLabel2=S�lectionnez les composants que vous d�sirez installer ; d�cochez les composants que vous ne d�sirez pas installer. Cliquez ensuite sur Suivant pour continuer l'installation.
FullInstallation=Installation compl�te
; if possible don't translate 'Compact' as 'Minimal' (I mean 'Minimal' in your language)
CompactInstallation=Installation compacte
CustomInstallation=Installation personnalis�e
NoUninstallWarningTitle=Composants existants
NoUninstallWarning=L'assistant d'installation a d�tect� que les composants suivants sont d�j� install�s sur votre syst�me :%n%n%1%n%nD�s�lectionner ces composants ne les d�sinstallera pas pour autant.%n%nVoulez-vous continuer malgr� tout ?
ComponentSize1=%1 Ko
ComponentSize2=%1 Mo
ComponentsDiskSpaceMBLabel=Les composants s�lectionn�s n�cessitent au moins [mb] Mo d'espace disponible.

; *** "Select Additional Tasks" wizard page
WizardSelectTasks=T�ches suppl�mentaires
SelectTasksDesc=Quelles sont les t�ches suppl�mentaires qui doivent �tre effectu�es ?
SelectTasksLabel2=S�lectionnez les t�ches suppl�mentaires que l'assistant d'installation doit effectuer pendant l'installation de [name], puis cliquez sur Suivant.

; *** "Select Start Menu Folder" wizard page
WizardSelectProgramGroup=S�lection du dossier du menu D�marrer
SelectStartMenuFolderDesc=O� l'assistant d'installation doit-il placer les raccourcis du programme ?
SelectStartMenuFolderLabel3=L'assistant va cr�er les raccourcis du programme dans le dossier du menu D�marrer indiqu� ci-dessous.
SelectStartMenuFolderBrowseLabel=Cliquez sur Suivant pour continuer. Cliquez sur Parcourir si vous souhaitez s�lectionner un autre dossier du menu D�marrer.
MustEnterGroupName=Vous devez saisir un nom de dossier du menu D�marrer.
GroupNameTooLong=Le nom ou le chemin du dossier est trop long.
InvalidGroupName=Le nom du dossier n'est pas valide.
BadGroupName=Le nom du dossier ne doit contenir aucun des caract�res suivants :%n%n%1
NoProgramGroupCheck2=Ne pas cr�er de &dossier dans le menu D�marrer

; *** "Ready to Install" wizard page
WizardReady=Pr�t � installer
ReadyLabel1=L'assistant dispose � pr�sent de toutes les informations pour installer [name] sur votre ordinateur.
ReadyLabel2a=Cliquez sur Installer pour proc�der � l'installation ou sur Pr�c�dent pour revoir ou modifier une option d'installation.
ReadyLabel2b=Cliquez sur Installer pour proc�der � l'installation.
ReadyMemoUserInfo=Informations sur l'utilisateur :
ReadyMemoDir=Dossier de destination :
ReadyMemoType=Type d'installation :
ReadyMemoComponents=Composants s�lectionn�s :
ReadyMemoGroup=Dossier du menu D�marrer :
ReadyMemoTasks=T�ches suppl�mentaires :

; *** "Preparing to Install" wizard page
WizardPreparing=Pr�paration de l'installation
PreparingDesc=L'assistant d'installation pr�pare l'installation de [name] sur votre ordinateur.
PreviousInstallNotCompleted=L'installation ou la suppression d'un programme pr�c�dent n'est pas totalement achev�e. Veuillez red�marrer votre ordinateur pour achever cette installation ou suppression.%n%nUne fois votre ordinateur red�marr�, veuillez relancer cet assistant pour reprendre l'installation de [name].
CannotContinue=L'assistant ne peut pas continuer. Veuillez cliquer sur Annuler pour abandonner l'installation.
ApplicationsFound=Les applications suivantes utilisent des fichiers qui doivent �tre mis � jour par l'assistant. Il est recommand� d'autoriser l'assistant � fermer ces applications automatiquement.
ApplicationsFound2=Les applications suivantes utilisent des fichiers qui doivent �tre mis � jour par l'assistant. Il est recommand� d'autoriser l'assistant � fermer ces applications automatiquement. Une fois l'installation termin�e, l'assistant essaiera de relancer ces applications.
CloseApplications=&Arr�ter les applications automatiquement
DontCloseApplications=&Ne pas arr�ter les applications
ErrorCloseApplications=L'assistant d'installation n'a pas pu arr�ter toutes les applications automatiquement. Nous vous recommandons de fermer toutes les applications qui utilisent des fichiers devant �tre mis � jour par l'assistant d'installation avant de continuer.

; *** "Installing" wizard page
WizardInstalling=Installation en cours
InstallingLabel=Veuillez patienter pendant que l'assistant installe [name] sur votre ordinateur.

; *** "Setup Completed" wizard page
FinishedHeadingLabel=Fin de l'installation de [name]
FinishedLabelNoIcons=L'assistant a termin� l'installation de [name] sur votre ordinateur.
FinishedLabel=L'assistant a termin� l'installation de [name] sur votre ordinateur. L'application peut �tre lanc�e � l'aide des ic�nes cr��es sur le Bureau par l'installation.
ClickFinish=Veuillez cliquer sur Terminer pour quitter l'assistant d'installation.
FinishedRestartLabel=L'assistant doit red�marrer votre ordinateur pour terminer l'installation de [name].%n%nVoulez-vous red�marrer maintenant ?
FinishedRestartMessage=L'assistant doit red�marrer votre ordinateur pour terminer l'installation de [name].%n%nVoulez-vous red�marrer maintenant ?
ShowReadmeCheck=Oui, je souhaite lire le fichier LISEZMOI
YesRadio=&Oui, red�marrer mon ordinateur maintenant
NoRadio=&Non, je pr�f�re red�marrer mon ordinateur plus tard
; used for example as 'Run MyProg.exe'
RunEntryExec=Ex�cuter %1
; used for example as 'View Readme.txt'
RunEntryShellExec=Voir %1

; *** "Setup Needs the Next Disk" stuff
ChangeDiskTitle=L'assistant a besoin du disque suivant
SelectDiskLabel2=Veuillez ins�rer le disque %1 et cliquer sur OK.%n%nSi les fichiers de ce disque se trouvent � un emplacement diff�rent de celui indiqu� ci-dessous, veuillez saisir le chemin correspondant ou cliquez sur Parcourir.
PathLabel=&Chemin :
FileNotInDir2=Le fichier "%1" ne peut pas �tre trouv� dans "%2". Veuillez ins�rer le bon disque ou s�lectionner un autre dossier.
SelectDirectoryLabel=Veuillez indiquer l'emplacement du disque suivant.

; *** Installation phase messages
SetupAborted=L'installation n'est pas termin�e.%n%nVeuillez corriger le probl�me et relancer l'installation.
EntryAbortRetryIgnore=Cliquez sur Recommencer pour essayer � nouveau, Ignorer pour continuer malgr� tout, ou Abandonner pour annuler l'installation.

; *** Installation status messages
StatusClosingApplications=Ferme les applications...
StatusCreateDirs=Cr�ation des dossiers...
StatusExtractFiles=Extraction des fichiers...
StatusCreateIcons=Cr�ation des raccourcis...
StatusCreateIniEntries=Cr�ation des entr�es du fichier INI...
StatusCreateRegistryEntries=Cr�ation des entr�es de registre...
StatusRegisterFiles=Enregistrement des fichiers...
StatusSavingUninstall=Sauvegarde des informations de d�sinstallation...
StatusRunProgram=Finalisation de l'installation...
StatusRestartingApplications=Relance les applications...
StatusRollback=Annulation des modifications...

; *** Misc. errors
ErrorInternal2=Erreur interne : %1
ErrorFunctionFailedNoCode=%1 a �chou�
ErrorFunctionFailed=%1 a �chou� ; code %2
ErrorFunctionFailedWithMessage=%1 a �chou� ; code %2.%n%3
ErrorExecutingProgram=Impossible d'ex�cuter le fichier :%n%1

; *** Registry errors
ErrorRegOpenKey=Erreur lors de l'ouverture de la cl� de registre :%n%1\%2
ErrorRegCreateKey=Erreur lors de la cr�ation de la cl� de registre :%n%1\%2
ErrorRegWriteKey=Erreur lors de l'�criture de la cl� de registre :%n%1\%2

; *** INI errors
ErrorIniEntry=Erreur d'�criture d'une entr�e dans le fichier INI "%1".

; *** File copying errors
FileAbortRetryIgnore=Cliquez sur Recommencer pour essayer � nouveau, Ignorer pour passer ce fichier (d�conseill�), ou Abandonner pour annuler l'installation.
FileAbortRetryIgnore2=Cliquez sur Recommencer pour essayer � nouveau, Ignorer pour continuer malgr� tout (d�conseill�), ou Abandonner pour annuler l'installation.
SourceIsCorrupted=Le fichier source est alt�r�
SourceDoesntExist=Le fichier source "%1" n'existe pas
ExistingFileReadOnly=Le fichier existant est prot�g� en lecture seule.%n%nCliquez sur Recommencer pour enlever la protection et essayer � nouveau, Ignorer pour passer ce fichier, ou Abandonner pour annuler l'installation.
ErrorReadingExistingDest=Une erreur s'est produite lors de la tentative de lecture du fichier existant :
FileExists=Le fichier existe d�j�.%n%nSouhaitez-vous que l'installation le remplace ?
ExistingFileNewer=Le fichier existant est plus r�cent que celui que l'assistant essaie d'installer. Il est recommand� de conserver le fichier existant.%n%nSouhaitez-vous conserver le fichier existant ?
ErrorChangingAttr=Une erreur est survenue en essayant de modifier les attributs du fichier existant :
ErrorCreatingTemp=Une erreur est survenue en essayant de cr�er un fichier dans le dossier de destination :
ErrorReadingSource=Une erreur est survenue lors de la lecture du fichier source :
ErrorCopying=Une erreur est survenue lors de la copie d'un fichier :
ErrorReplacingExistingFile=Une erreur est survenue lors du remplacement d'un fichier existant :
ErrorRestartReplace=Le marquage d'un fichier pour remplacement au red�marrage de l'ordinateur a �chou� :
ErrorRenamingTemp=Une erreur est survenue en essayant de renommer un fichier dans le dossier de destination :
ErrorRegisterServer=Impossible d'enregistrer la biblioth�que DLL/OCX : %1
ErrorRegSvr32Failed=RegSvr32 a �chou� et a retourn� le code d'erreur %1
ErrorRegisterTypeLib=Impossible d'enregistrer la biblioth�que de type : %1

; *** Post-installation errors
ErrorOpeningReadme=Une erreur est survenue � l'ouverture du fichier LISEZMOI.
ErrorRestartingComputer=L'installation n'a pas pu red�marrer l'ordinateur. Merci de bien vouloir le faire vous-m�me.

; *** Uninstaller messages
UninstallNotFound=Le fichier "%1" n'existe pas. Impossible de d�sinstaller.
UninstallOpenError=Le fichier "%1" n'a pas pu �tre ouvert. Impossible de d�sinstaller
UninstallUnsupportedVer=Le format du fichier journal de d�sinstallation "%1" n'est pas reconnu par cette version de la proc�dure de d�sinstallation. Impossible de d�sinstaller
UninstallUnknownEntry=Une entr�e inconnue (%1) a �t� rencontr�e dans le fichier journal de d�sinstallation
ConfirmUninstall=Voulez-vous vraiment d�sinstaller compl�tement %1 ainsi que tous ses composants ?
UninstallOnlyOnWin64=La d�sinstallation de ce programme ne fonctionne qu'avec une version 64 bits de Windows.
OnlyAdminCanUninstall=Ce programme ne peut �tre d�sinstall� que par un utilisateur disposant des droits d'administration.
UninstallStatusLabel=Veuillez patienter pendant que %1 est retir� de votre ordinateur.
UninstalledAll=%1 a �t� correctement d�sinstall� de cet ordinateur.
UninstalledMost=La d�sinstallation de %1 est termin�e.%n%nCertains �l�ments n'ont pas pu �tre supprim�s automatiquement. Vous pouvez les supprimer manuellement.
UninstalledAndNeedsRestart=Vous devez red�marrer l'ordinateur pour terminer la d�sinstallation de %1.%n%nVoulez-vous red�marrer maintenant ?
UninstallDataCorrupted=Le ficher "%1" est alt�r�. Impossible de d�sinstaller

; *** Uninstallation phase messages
ConfirmDeleteSharedFileTitle=Supprimer les fichiers partag�s ?
ConfirmDeleteSharedFile2=Le syst�me indique que le fichier partag� suivant n'est plus utilis� par aucun programme. Souhaitez-vous que la d�sinstallation supprime ce fichier partag� ?%n%nSi des programmes utilisent encore ce fichier et qu'il est supprim�, ces programmes ne pourront plus fonctionner correctement. Si vous n'�tes pas s�r, choisissez Non. Laisser ce fichier dans votre syst�me ne posera pas de probl�me.
SharedFileNameLabel=Nom du fichier :
SharedFileLocationLabel=Emplacement :
WizardUninstalling=�tat de la d�sinstallation
StatusUninstalling=D�sinstallation de %1...

; *** Shutdown block reasons
ShutdownBlockReasonInstallingApp=Installe %1.
ShutdownBlockReasonUninstallingApp=D�sinstalle %1.

; Les messages personnalis�s suivants ne sont pas utilis� par l'installation
; elle-m�me, mais si vous les utilisez dans vos scripts, vous devez les
; traduire

[CustomMessages]

NameAndVersion=%1 version %2
AdditionalIcons=Ic�nes suppl�mentaires :
CreateDesktopIcon=Cr�er une ic�ne sur le &Bureau
CreateQuickLaunchIcon=Cr�er une ic�ne dans la barre de &Lancement rapide
ProgramOnTheWeb=Page d'accueil de %1
UninstallProgram=D�sinstaller %1
LaunchProgram=Ex�cuter %1
AssocFileExtension=&Associer %1 avec l'extension de fichier %2
AssocingFileExtension=Associe %1 avec l'extension de fichier %2...
AutoStartProgramGroupDescription=D�marrage :
AutoStartProgram=D�marrer automatiquement %1
AddonHostProgramNotFound=%1 n'a pas �t� trouv� dans le dossier que vous avez choisi.%n%nVoulez-vous continuer malgr� tout ?
GenerateQrCode=Generer un QR-code
Settings=Parametres
Update=Mettre a jour
CopyMenu=Copier le lien
CopyQRMenu=Copier le code QR
WeblocLink=.webloc lien
Webarchive=Archives Web
Warning=Avertissement!
Java17InstallWarning=Cette version de WeblocOpener nécessite Java 17 ! Souhaitez-vous pré-installer Java 17 depuis adoptium.net?