from django.urls import path
from . import views

urlpatterns = [
    path('upload/', views.upload_voice, name='upload-voice-view'),
    path('get_profile_voices/', views.get_profile_voices, name='get-profile-voices-view'),
]
