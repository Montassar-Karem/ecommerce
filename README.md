# E-commerce API — Catalogue & Panier

![CI](https://github.com/Montassar-Karem/ecommerce/actions/workflows/ci.yml/badge.svg)

API REST de gestion d'un catalogue de produits (catégories arborescentes) et d'un panier d'achat.

## Stack
- Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation)
- PostgreSQL 16
- MapStruct, Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, AssertJ, Testcontainers
- Docker, Docker Compose, GitHub Actions

## Lancer le projet

**Prérequis : Docker uniquement.**

```bash
git clone https://github.com/Montassar-Karem/ecommerce.git
cd ecommerce
docker compose up --build -d
```

- Swagger UI : http://localhost:8080/swagger-ui.html
- OpenAPI : http://localhost:8080/v3/api-docs

### En développement
```bash
docker compose up -d db      # base seule
./mvnw spring-boot:run       # application depuis le poste
```

### Tests
```bash
./mvnw verify                # Docker doit tourner (Testcontainers)
```

## Endpoints

### Catalogue (User Story 1)
| Méthode | URL | Description |
|---|---|---|
| POST | `/api/categories` | Créer une catégorie (`parentId` optionnel → sous-catégorie) |
| GET | `/api/categories` | Arbre complet des catégories |
| GET | `/api/categories/{id}` | Une catégorie et sa sous-arborescence |
| PUT | `/api/categories/{id}` | Modifier nom / description |
| DELETE | `/api/categories/{id}` | Supprimer (sous-catégories incluses, produits conservés) |
| POST | `/api/categories/{id}/subcategories/{childId}` | Lier / déplacer une sous-catégorie |
| DELETE | `/api/categories/{id}/subcategories/{childId}` | Délier (la catégorie devient racine) |
| POST | `/api/categories/{id}/products/{productId}` | Lier un produit |
| DELETE | `/api/categories/{id}/products/{productId}` | Délier un produit |
| POST / GET | `/api/products` | Créer / lister les produits |
| GET / PUT / DELETE | `/api/products/{id}` | Consulter / modifier / supprimer un produit |

### Panier (User Story 2)
| Méthode | URL | Description |
|---|---|---|
| POST | `/api/carts` | Créer un panier |
| GET | `/api/carts/{id}` | Panier avec lignes, sous-totaux et total |
| GET | `/api/carts/{id}/total` | Total du panier |
| POST | `/api/carts/{id}/items` | Ajouter un ou plusieurs produits (quantités cumulées) |
| PUT | `/api/carts/{id}/items` | Modifier une ou plusieurs quantités (0 = retirer) |
| DELETE | `/api/carts/{id}/items/{productId}` | Retirer un produit |
| DELETE | `/api/carts/{id}/items?productIds=1,2` | Retirer plusieurs produits |

Exemple d'ajout :
```json
POST /api/carts/1/items
{ "items": [ { "productId": 1, "quantity": 2 }, { "productId": 2, "quantity": 1 } ] }
```

## Tests
- **Domaine** : règles de l'arbre et du panier, sans Spring
- **Services** : Mockito
- **Controllers** : `@WebMvcTest` (codes HTTP, validation, format JSON)
- **Intégration** : Testcontainers PostgreSQL, scénario complet de bout en bout

## Pistes d'amélioration
- Pagination sur la liste des produits
- Sécurité (Spring Security + JWT) et panier rattaché à un utilisateur
