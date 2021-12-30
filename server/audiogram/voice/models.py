from django.db import models
import django.utils.timezone as timezone
from user.models import User


class Voice(models.Model):
    owner = models.ForeignKey(User, on_delete=models.CASCADE)
    voice = models.FileField(upload_to='voices')
    publish_date = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"{self.owner}: {self.voice}"


class Like(models.Model):
    voice = models.ForeignKey(Voice, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    like_date = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f'{self.user} liked voice"{self.voice}"'


class Comment(models.Model):
    voice = models.ForeignKey(Voice, on_delete=models.CASCADE)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    comment_date = models.DateTimeField(default=timezone.now)
    content = models.TextField()

    def __str__(self):
        return f'{self.user} commented on voice"{self.voice}"'
