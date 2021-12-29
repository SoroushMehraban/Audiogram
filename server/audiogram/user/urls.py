from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.login_view, name='login-view'),
    path('search/', views.search_users, name='search-view'),
    path('upload_profile_image/', views.upload_profile_image, name='profile-image-view'),
    path('info/', views.get_info, name='info-view'),
    path('follow/', views.follow, name='follow-view'),
    path('unfollow/', views.unfollow, name='unfollow-view'),
    path('sign_up/', views.sign_up_view, name='signup-view'),
]
