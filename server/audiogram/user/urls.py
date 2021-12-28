from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.login_view, name='login-view'),
    path('search/', views.search_users, name='search-view'),
    path('info/', views.get_info, name='info-view'),
    path('sign_up/', views.sign_up_view, name='signup-view'),
]
