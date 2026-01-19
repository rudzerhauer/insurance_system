import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';
import { Policies } from './policies/policies';
import { MyPolicies } from './my-policies/my-policies';
import { Verify } from './verify/verify';
import { SsoTest } from './sso-test/sso-test';
const routes: Routes = [
    {path: 'login', component : Login},
    {path: 'register', component : Register},
    {path : 'policies', component : Policies},
    {path : 'my-policies', component : MyPolicies},
    {path: 'verify', component: Verify},
  {path: 'sso-test', component: SsoTest},
    {path:'', redirectTo: 'login', pathMatch: 'full'}
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
