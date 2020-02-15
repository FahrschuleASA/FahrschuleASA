import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {UserRouteAccessService} from "../core/auth/user-route-access-service";

/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

@NgModule({
    imports: [
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
        RouterModule.forChild([
            {
                path: 'admin-management',
                loadChildren: () => import('./admin-management/admin-management.module').then(m => m.AdminManagementModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'student-management',
                loadChildren: () => import('./student-management/student-management.module').then(m => m.StudentManagementModule),
                data: {
                    authorities: ['ROLE_ADMIN', 'ROLE_TEACHER']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'teacher-management',
                loadChildren: () => import('./teacher-management/teacher-management.module').then(m => m.TeacherManagementModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'user-management',
                loadChildren: () => import('./user-management/user-management.module').then(m => m.UserManagementModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'audits',
                loadChildren: () => import('./audits/audits.module').then(m => m.AuditsModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'configuration',
                loadChildren: () => import('./configuration/configuration.module').then(m => m.ConfigurationModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'driving-school-configuration',
                loadChildren: () => import('./driving-school-configuration/driving-school-configuration.module').then(m => m.DrivingSchoolConfigurationModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'docs',
                loadChildren: () => import('./docs/docs.module').then(m => m.DocsModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'health',
                loadChildren: () => import('./health/health.module').then(m => m.HealthModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'logs',
                loadChildren: () => import('./logs/logs.module').then(m => m.LogsModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'tracker',
                loadChildren: () => import('./tracker/tracker.module').then(m => m.TrackerModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            },
            {
                path: 'metrics',
                loadChildren: () => import('./metrics/metrics.module').then(m => m.MetricsModule),
                data: {
                    authorities: ['ROLE_ADMIN']
                },
                canActivate: [UserRouteAccessService]
            }
            /* jhipster-needle-add-admin-route - JHipster will add admin routes here */
        ])
    ]
})
export class AdminRoutingModule {
}
