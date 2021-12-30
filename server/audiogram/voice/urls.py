from django.urls import path
from . import views

urlpatterns = [
    path('upload/', views.upload_voice, name='upload-voice-view'),
    path('get_profile_voices/', views.get_profile_voices, name='get-profile-voices-view'),
    path('get_home_voices/', views.get_home_voices, name='get-home-voices-view'),
    path('like/', views.like_view, name='like-view'),
    path('get_comments/', views.get_comments, name='get-comments-view'),
    path('comment/', views.comment_view, name='get-comments-view'),
]
