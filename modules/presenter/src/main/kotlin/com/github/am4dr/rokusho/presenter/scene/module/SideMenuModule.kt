package com.github.am4dr.rokusho.presenter.scene.module

import com.github.am4dr.rokusho.presenter.Presenter
import com.github.am4dr.rokusho.presenter.scene.CharacterIconSideMenu

class SideMenuModule(
    presenter: Presenter
) {

    val node: CharacterIconSideMenu = CharacterIconSideMenu(
        presenter.libraries,
        presenter::select,
        presenter.selectedLibrary,
        presenter::chooseAndAddLibraryByPath
    )
}