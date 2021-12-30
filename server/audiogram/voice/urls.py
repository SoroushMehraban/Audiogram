from django.urls import path
from . import views

urlpatterns = [
    path('upload/', views.upload_voice, name='upload-voice-view'),
]
