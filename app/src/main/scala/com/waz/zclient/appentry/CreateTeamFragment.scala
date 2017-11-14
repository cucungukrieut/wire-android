/**
 * Wire
 * Copyright (C) 2017 Wire Swiss GmbH
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
 */
package com.waz.zclient.appentry

import android.os.Bundle
import android.support.transition._
import android.view.{LayoutInflater, View, ViewGroup}
import com.waz.zclient.appentry.CreateTeamFragment._
import com.waz.zclient.appentry.scenes._
import com.waz.zclient.pages.BaseFragment
import com.waz.zclient.{AppEntryController, FragmentHelper, OnBackPressedListener}
import com.waz.zclient.AppEntryController._
import com.waz.zclient.R
import com.waz.ZLog.ImplicitTag.implicitLogTag

class CreateTeamFragment extends BaseFragment[Container] with FragmentHelper with OnBackPressedListener {

  lazy val appEntryController = inject[AppEntryController]

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View =
    inflater.inflate(R.layout.app_entry_fragment, container, false)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {

    appEntryController.entryStage.onUi { state =>
      val entryScene = state match {
        case NoAccountState(FirstScreen) => FirstScreenSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case NoAccountState(RegisterTeamScreen) => TeamNameSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case SetTeamEmail => SetEmailSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case VerifyTeamEmail => VerifyEmailSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case SetUsersNameTeam => SetNameSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case SetPasswordTeam => SetPasswordSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
        case SetUsernameTeam => SetUsernameSceneController(getView.asInstanceOf[ViewGroup])(getContext, this, injector)
      }
      TransitionManager.go(entryScene.scene, new SupportAutoTransition2())
      entryScene.onCreate()
    }

  }

  override def onBackPressed(): Boolean = {
    if (appEntryController.entryStage.currentValue.exists(_ != NoAccountState(FirstScreen))) {
      appEntryController.cancelCreateTeam()
      true
    } else
      false
  }
}

object CreateTeamFragment {
  val TAG: String = classOf[CreateTeamFragment].getName

  def newInstance = new CreateTeamFragment

  trait Container
}

class SupportAutoTransition2 extends TransitionSet {
  setOrdering(TransitionSet.ORDERING_TOGETHER)
  addTransition(new Fade(Fade.OUT)).addTransition(new ChangeBounds).addTransition(new Fade(Fade.IN))
}
