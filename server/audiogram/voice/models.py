from django.db import models

from user.models import User


class Voice(models.Model):
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    voice = models.FileField(upload_to='voices')

    def __str__(self):
        return f"{self.owner}: {self.voice}"

