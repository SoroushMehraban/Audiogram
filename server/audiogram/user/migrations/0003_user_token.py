# Generated by Django 3.2.4 on 2021-12-28 20:50

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('user', '0002_follow'),
    ]

    operations = [
        migrations.AddField(
            model_name='user',
            name='token',
            field=models.CharField(default='', max_length=64),
        ),
    ]