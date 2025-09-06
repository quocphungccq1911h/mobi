import { inject } from "@angular/core"
import { CanActivateFn } from "@angular/router"
import { AuthService } from "./auth.service"

export const roleGuard = (roles: string[]): CanActivateFn => () => {
    const auth = inject(AuthService);
    return roles.some(r=>auth.hasRole(r));
}