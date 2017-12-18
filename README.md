# pvpstats
Fork from [slipcor](https://github.com/slipcor/PVPStats)'s repo. Fix db lag and remove unneeded command.

## What's new
- Async query and store.
- Add new placeholder `pvptop_<order_by>_<order>_<field>`. Syntax like `%pvptop_elo_1_elo%`. query key cache 1 hour.'