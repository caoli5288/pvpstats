# pvpstats
Fork from [slipcor](https://github.com/slipcor/PVPStats)'s repo. Fix db lag and remove unneeded command.

## What's new
- Async query and store.
- Add new placeholder `pvptop_<order_by>_<order>_<field>`. Syntax like `%pvptop_elo_1_elo%`. query key cache 1 hour.
- Add extra score and score exchange system.

## Exchange system
We add a new score system top of the original elo system and the score will grow as elo grow. The score can be used to exchange item defined in the `config.yml`.
```yaml
exchange:
- id: 'sample'
  command: 'kill %player_name%'
  price: 100
  permission: '' # the permission required to exchange. not needed.
- id: 'multi-command-sample'
  command: |
    'say %player_name% 兑换了一份pvp积分礼包'
    'xkit kit kit1 %player_name%'
  price: 1000
  permission: 'pvpexchange.sample'
```

Type `/pvpexchange <id>` to exchange.