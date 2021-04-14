import { NgModule } from '@angular/core';
import { RouterModule, Routes} from '@angular/router'
//import { AppComponent } from './app.component'
//import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  //{ path:"", component: AppComponent, pathMatch:"full"},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
