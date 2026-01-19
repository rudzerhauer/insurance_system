import { NgModule, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Register } from './register/register';
import { Verify } from './verify/verify';
import { Policies } from './policies/policies';
import { MyPolicies } from './my-policies/my-policies';
import { SsoTest } from './sso-test/sso-test';
import { AuthInterceptor } from './services/auth-interceptor';
@NgModule({
  declarations: [
    App,
    Dashboard,
    Login,
    Register,
    Verify,
    Policies,
    MyPolicies,
    SsoTest
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
  ],
  bootstrap: [App]
})
export class AppModule { }
