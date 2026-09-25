# PR Check Repro Fixture v2 — Case 00137591 (New York Life)

This replaces the earlier log4j-based fixture. This version uses the **same
two dependencies as the customer's real upgrade** — `spring-boot-starter-web`
and `newrelic-agent` — and mirrors the customer's actual branch topology:

```
main  (monitored)  ──PR#1 (clean)──▶ qa   (monitored)  ──PR#2 (flags issues)──▶ main
upgrade (unmonitored, base branch customer forked from)
```

Two POM files are included:
- `pom.xml` — the **baseline** state (push this first, this is what `main`
  starts on).
- `pom-upgraded.xml` — the **upgraded** state. Copy its `<dependencies>`
  block over `pom.xml`'s in the upgrade PR (step 3 below). It's a version-only
  bump of the same two artifacts — no dependencies added or removed — same
  shape as the customer's real PR.

Delete the old log4j-based project in the Snyk UI before starting this one so
results aren't cross-contaminated.

## Steps

1. **Push baseline.** Push this repo as-is (with `pom.xml` as committed,
   ignore `pom-upgraded.xml` for now) to your GitHub test org, default branch
   `main`.
2. **Import only `main`** as a monitored Snyk Project — mirrors the
   customer's setup (only `main` was monitored; their `qa_26_sep` and
   `upgrade` branches were not, until later).
3. **Create `upgrade` branch off `main`** (unmonitored, matches customer).
   From `upgrade`, create a `qa` branch. Open a PR from `qa` into... actually
   simplest: open PR **`qa` → `main`** is what you'll do in step 5. For now,
   on `qa`, replace `pom.xml`'s `<dependencies>` block with the one from
   `pom-upgraded.xml` (the version bump) and commit directly to `qa` — no PR
   needed yet, since `qa` isn't monitored and doesn't need a check here.
4. **Open PR `qa` → `main`.** This is the PR that matters — it's the one
   that carries the version bump into the only monitored branch. Watch what
   the PR check reports (severity counts, fail/pass, and whatever "Fail
   Conditions" label shows on the result page).
5. **Merge it.** Re-check `main`'s Snyk Project after merge — issue count
   should match (or nearly match) what the PR check reported. This is the
   moment to compare against the case: did the check catch the same
   Spring/New Relic findings before merge that later showed up as "new" on
   `main`, or did something slip through?
6. **Optional — isolate PR-check-specific gaps.** Run `snyk test` locally
   against `pom-upgraded.xml`'s dependency set, outside of PR checks
   entirely. If the CLI flags issues the PR check didn't, that's a PR-check
   -specific gap, not a general scanning gap.

## What this fixture can and can't prove

It **can** show, cleanly, that a version-only bump (no new dependencies) on
these exact packages produces a real, non-trivial set of findings today —
useful for demonstrating the mechanism to the customer even without
recreating exact dates.

It **cannot** recreate the actual timing gap from the case — the leading
theory is that specific CVEs affecting these versions were only *disclosed*
publicly between the customer's first PR check (Aug 24) and their merge to
main (Sep 23–24), so their first check legitimately found nothing to flag.
That's a disclosure-calendar fact, not something a fresh repro run today can
reproduce — whatever this fixture flags right now reflects today's
vulnerability database, not the state as of Aug 24.

## Safety

These are real, currently-known-vulnerable versions of real Maven Central
artifacts. Don't deploy this anywhere reachable, and remove/upgrade the
dependencies once you're done testing.
