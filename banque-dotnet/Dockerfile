# Étape 1 : Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app

COPY BanqueDepot.sln ./
COPY BanqueDepot/*.csproj BanqueDepot/
RUN dotnet restore BanqueDepot/BanqueDepot.csproj

COPY . ./
RUN dotnet publish BanqueDepot/BanqueDepot.csproj -c Release -o out

# Étape 2 : Runtime
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
COPY --from=build /app/out ./

# Configurer le port via variable d'environnement
ENV ASPNETCORE_URLS=http://+:5555

EXPOSE 5555
ENTRYPOINT ["dotnet", "BanqueDepot.dll"]


# docker build -t banque-depot .

# docker stop banque-depot
# docker rm banque-depot 

# sans utiliser la base de donnée local 
# docker run -d -p 5555:5555 --name banque-depot banque-depot

# pour utiliser la base de données local 
# docker run -d -p 5555:5555 --add-host=host.docker.internal:host-gateway --name banque-depot banque-depot
# dans appsettings.json 
    # docker "DefaultConnection": "Host=host.docker.internal;Database=banque;Username=postgres;Password=root"
    # original "DefaultConnection": "Host=localhost;Database=banque;Username=postgres;Password=root"

    #Ovaina koa ny port ao @ BanqueDepot.http, launchSettings.json, banque-centralisateur.service