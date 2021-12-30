import os

from PIL import Image, ImageOps
from django.contrib.auth.models import AbstractUser
from django.db import models

from audiogram import settings


class User(AbstractUser):
    image = models.ImageField(default='default_profile.png', upload_to='profiles')
    token = models.CharField(max_length=64, default="")


class Follow(models.Model):
    follower = models.ForeignKey(User, on_delete=models.CASCADE, related_name='follower')
    following = models.ForeignKey(User, on_delete=models.CASCADE, related_name='following')

    def __str__(self):
        return f"{self.follower} -> {self.following}"
