# Star Runner (Browser Game)

A lightweight, browser-based arcade game built with HTML, CSS, and vanilla
JavaScript. Move left/right, dodge asteroids, and collect bonus stars.

## Run locally

You can open the file directly:

1. Open `web-game/index.html` in your browser.

Or use a small local server (recommended):

```bash
python3 -m http.server 8000
```

Then open:

```
http://localhost:8000/web-game/
```

## Controls

- Move: Arrow keys or A/D
- Start or resume: Space
- Pause: P
- Restart: R
- Touch: drag on the game area

## Free deployment options (learning use)

### Option 1: GitHub Pages (simplest)

1. Create a new GitHub repository.
2. Copy the contents of `web-game/` into the root of the new repo.
3. Push the repo to GitHub.
4. In **Settings -> Pages**, set:
   - Source: `Deploy from a branch`
   - Branch: `main` (or `master`)
   - Folder: `/ (root)`
5. Your game will be available at:
   `https://<your-username>.github.io/<repo-name>/`

### Option 2: Cloudflare Pages or Netlify

Both support free static hosting and let you deploy a subfolder.

- **Cloudflare Pages**
  - Build command: none
  - Output directory: `web-game`

- **Netlify**
  - Build command: none
  - Publish directory: `web-game`

No backend is required for this game.
