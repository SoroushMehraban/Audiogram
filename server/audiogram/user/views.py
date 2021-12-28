import django.db.utils
from django.contrib.auth import logout, login, authenticate
from .models import User
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
            return JsonResponse({"success": True, "message": ""})
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
            user = User.objects.create_user(username=input_data['username'], email=input_data['email'],
                                            password=input_data['password'], first_name=input_data['first_name'],
                                            last_name=input_data['last_name'])
            user.save()
        except django.db.utils.IntegrityError:
            return JsonResponse({"success": False, "message": "User already exists"})

        return JsonResponse({'success': True, 'message': "Registered successfully"})

    return HttpResponse("Signup Page")
