# Generated by Django 4.0.4 on 2022-05-24 15:44

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('mobile_app', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='humaninfo',
            name='phone_number',
            field=models.CharField(max_length=30),
        ),
    ]
