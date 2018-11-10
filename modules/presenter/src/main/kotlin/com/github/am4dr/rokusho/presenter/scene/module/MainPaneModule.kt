package com.github.am4dr.rokusho.presenter.scene.module

import com.github.am4dr.rokusho.presenter.Presenter
import com.github.am4dr.rokusho.presenter.scene.MainPane

class MainPaneModule(
    presenter: Presenter,
    sideMenu: SideMenuModule
) {

    val node: MainPane = MainPane().apply {
        addLibraryEventHandler.set { presenter.chooseAndAddLibraryByPath() }
        libraryViewer.bind(presenter.selectedViewer)
        showAddLibraryMessage.bind(presenter.selectedLibrary.isNull)
        this.sideMenu.set(sideMenu.node)
    }
}