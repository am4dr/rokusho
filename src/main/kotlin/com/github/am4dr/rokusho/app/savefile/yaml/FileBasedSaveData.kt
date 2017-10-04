package com.github.am4dr.rokusho.app.savefile.yaml

import com.github.am4dr.rokusho.app.savedata.SaveData
import java.nio.file.Path

class FileBasedSaveData(val savefilePath: Path, val data: SaveData)