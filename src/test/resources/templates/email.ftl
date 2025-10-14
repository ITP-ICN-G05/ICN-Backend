<!DOCTYPE html>
<html lang="au-EN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ICN Navigator email varify </title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            padding: 20px;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            text-align: center;
        }

        p {
            color: #666;
            font-size: 16px;
            line-height: 1.6;
        }

        .highlight {
            color: #007bff;
            font-weight: bold;
        }

        .code-container {
            background-color: #f0f0f0;
            padding: 10px;
            text-align: center;
            border-radius: 5px;
            margin-top: 10px;
        }

        .code {
            font-size: 24px;
            font-weight: bold;
            margin: 0;
            padding: 5px;
        }

        .company-link {
            color: #007bff;
            text-decoration: none;
        }

        .footer {
            background: linear-gradient(to right, #007bff, #00bfff);
            height: 10px;
            border-radius: 5px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Dear customer</h1>

    <p>you have recived a email from <a href="${companyWebsite! 'ICN.Navigator.au'}" class="company-link">${companyName! 'ICN Navigator'}</a>
        , for the validation of your email address.</p>

    <div class="code-container">
        <p class="code">${verifyCode}</p>
    </div>

    <p>please user the above code for your validation ( ${validTime} mins before expire ).</p>

    <p>If you did not requested this code, please ignore it</p>

    <p>thanks for your patient</p>

    <p><a href="${companyWebsite! 'ICN.Navigator.au'}" class="company-link">${companyName! 'ICN Navigator'}</a></p>

    <div class="footer"></div>
</div>
</body>
</html>