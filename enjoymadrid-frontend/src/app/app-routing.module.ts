import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadChildren: () => import('./pages/index/index.module').then( m => m.IndexPageModule)
  },
  {
    path: 'create-route',
    loadChildren: () => import('./pages/create-route/create-route.module').then( m => m.CreateRoutePageModule)
  },
  {
    path: 'user-comments',
    loadChildren: () => import('./pages/user-comments/user-comments.module').then( m => m.UserCommentsPageModule)
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule)
  },
  {
    path: '**',
    loadChildren: () => import('./pages/page-not-found/page-not-found.module').then( m => m.PageNotFoundPageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
