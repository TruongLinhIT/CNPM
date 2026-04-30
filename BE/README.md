# Restaurant Management API

## Prerequisites

- Node.js 18+
- MySQL 8+

## Setup

1. Copy `.env.example` to `.env` and update values.
2. Install dependencies:
   - `npm install`
3. Start development server:
   - `npm run dev`

## Notes

- Set `DB_SYNC=true` only for local development if you want Sequelize to sync models.
- All API responses follow: `{ success, message, data, error }`.
