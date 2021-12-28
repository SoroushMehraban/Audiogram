import hashlib

import django.db.utils
from django.contrib.auth import logout, login, authenticate

from voice.models import Voice
from .models import User, Follow
from django.core.exceptions import ValidationError
from django.core.validators import validate_email
from django.http import JsonResponse, HttpResponse

from audiogram.utils import check_missing_data


def login_view(request):
    if request.method == "POST":
        input_data = {
            'username': request.POST.get('username'),
            'password': request.POST.get('password')
        }

        data_is_missing, missing_data = check_missing_data(input_data)
        if data_is_missing:
            return JsonResponse({"success": False, "message": missing_data})

        user = authenticate(request, username=input_data['username'], password=input_data['password'])
        if user is not None:
            login(request, user)
            return JsonResponse({"success": True, "message": user.token})
        else:
            return JsonResponse({"success": False, "message": "Username or password is wrong"})

    return HttpResponse("Login Page")


def logout_view(request):
    if request.method == "POST":
        logout(request)
        return JsonResponse({"success": True, "message": ""})

    return HttpResponse("Logout Page")


def sign_up_view(request):
    if request.method == "POST":
        input_data = {
            'first_name': request.POST.get('first_name'),
            'last_name': request.POST.get('last_name'),
            'email': request.POST.get('email'),
            'username': request.POST.get('username'),
            'password': request.POST.get('password'),
            'password_confirmation': request.POST.get('password_confirmation'),
        }

        data_is_missing, missing_data = check_missing_data(input_data)
        if data_is_missing:
            return JsonResponse({"success": False, "message": missing_data})

        try:
            validate_email(input_data['email'])
        except ValidationError:
            return JsonResponse({"success": False, "message": "Email format is wrong"})

        if input_data['password'] != input_data['password_confirmation']:
            return JsonResponse({"success": False, "message": "Passwords do not match"})

        try:
            m = hashlib.sha256()
            m.update(f"{input_data['username']}:{input_data['password']}".encode('utf-8'))
            token = m.hexdigest()
            user = User.objects.create_user(username=input_data['username'], email=input_data['email'],
                                            password=input_data['password'], first_name=input_data['first_name'],
                                            last_name=input_data['last_name'], token=token)
            user.save()
        except django.db.utils.IntegrityError:
            return JsonResponse({"success": False, "message": "User already exists"})

        return JsonResponse({'success': True, 'message': "Registered successfully"})

    return HttpResponse("Signup Page")


def get_info(request):
    if request.method == "POST":
        try:
            print(request.POST)
            user = User.objects.get(token=request.POST.get("token"))
        except Exception:
            return JsonResponse({"success": False, "message": "User is not authenticated"})

        username = request.POST.get("username")
        if username is not None:
            try:
                user = User.objects.get(username=username)
            except Exception:
                return JsonResponse({"success": False, "message": "User not exists"})

        print({
            "success": True,
            "username": user.username,
            "first_name": user.first_name,
            "last_name": user.last_name,
            "followers": Follow.objects.filter(following=user).count(),
            "followings": Follow.objects.filter(follower=user).count(),
            'voices': Voice.objects.filter(owner=user).count()
        })
        return JsonResponse({
            "success": True,
            "username": user.username,
            "firstName": user.first_name,
            "lastName": user.last_name,
            "followers": Follow.objects.filter(following=user).count(),
            "followings": Follow.objects.filter(follower=user).count(),
            'voices': Voice.objects.filter(owner=user).count()
        })
