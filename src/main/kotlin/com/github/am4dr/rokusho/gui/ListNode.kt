package com.github.am4dr.rokusho.gui

import com.github.am4dr.rokusho.app.ImageItem
import javafx.collections.ObservableList
import javafx.scene.control.ListView

class ListNode(list: ObservableList<ImageItem>) : ListView<ImageItem>(list)