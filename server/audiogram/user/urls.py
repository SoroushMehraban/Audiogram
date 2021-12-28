from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.login_view, name='login-view'),
    path('info/', views.get_info, name='login-view'),
    path('logout/', views.logout_view, name='logout-view'),
    path('sign_up/', views.sign_up_view, name='signup-view'),
]
