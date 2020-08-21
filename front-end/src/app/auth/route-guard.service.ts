import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot} from '@angular/router';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot): boolean {

    // this will be passed from the route config
    // on the data property
    const expectedRole = route.data.expectedRole;

    const rolesStr = localStorage.getItem('roles');
    const roles = rolesStr ? rolesStr.split(',') : [];

    if (!this.authService.isLoggedIn() || roles.filter((r) => r === expectedRole).length < 1) {
      this.router.navigate(['home'], {queryParams: {doLogin: 'true'}});
      return false;
    }
    return true;
  }
}