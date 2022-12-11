package com.github.benchdoos.weblocopener.domain;

import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;

public record ExtendedModificationInfo(ModificationType type, UpdateInfo.Modification modification) {
    public enum ModificationType {
        FEATURE, IMPROVEMENT, BUGFIX
    }
}
