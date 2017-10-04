package com.github.am4dr.rokusho.app.savedata.store

interface SaveDataSerializer<in T> : (T) -> ByteArray
