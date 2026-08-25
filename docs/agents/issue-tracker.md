# Issue tracker

Issues and specs for this repository live in GitHub Issues under `StoneHub/screen-timeout-tile`. Use the authenticated `gh` CLI from this checkout.

## Operations

- Create: `gh issue create --repo StoneHub/screen-timeout-tile`
- Read: `gh issue view <number> --repo StoneHub/screen-timeout-tile --comments`
- List: `gh issue list --repo StoneHub/screen-timeout-tile`
- Comment: `gh issue comment <number> --repo StoneHub/screen-timeout-tile`
- Label or close: use `gh issue edit` or `gh issue close` with the same explicit repository.

## Pull requests

Pull requests are not a triage request source. Inspect a PR when an issue, commit, or user request points to it.

## Skill routing

When a skill says to publish a ticket, create a GitHub issue. When it says to fetch a ticket, read the issue and its comments. Use GitHub sub-issues and native issue dependencies when a multi-ticket plan needs parent or blocking relationships.
